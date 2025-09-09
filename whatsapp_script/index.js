/*
connects to webwhatsapp to send 
reminders for pending assignments
*/ 


//obtain necessary modules
//1. generate qrcode for login
const qrcode = require('qrcode-terminal');

//2. get client duh for whatsapp, LocalAuth to store login session locally 
const { Client, LocalAuth, AuthStrategy} = require('whatsapp-web.js');
const client = new Client({
     authStrategy : new LocalAuth({
        datapath: './'
     })
});

//3. to resovlve the filepath for 'Pending.json'
const path = require("path");

//4. file system module to check if Pending.json is updated/changed
const fs = require("fs");


//global vars
let assignments = null;  //contains current list of pending assignments
const chatName = "Test";   //Chat that will recieve the reminders
let chatId = null;  //grp/chat that will get the notifications ie reminders
const reminders = new Map(); //contains mapping of assigment : schedule reminders fns()

//resolving filepath to Pending.json
const filepath = path.resolve(__dirname,"../Data/Pending.json");

//generate QR code for login
client.on('qr', (qr) => {
    qrcode.generate(qr, { small : true })
});


client.on('auth_failure', async (msg) => {
    console.error("Auth failed",msg);
});


//when client logs in get chatId and and exsiting list of assignments
client.on('ready', async () => {
    console.log('Client is ready!');

    if(fs.existsSync(path.resolve(__dirname,"chatId.txt"))){
        chatId = fs.readFileSync(path.resolve(__dirname,"chatId.txt"), 'utf-8').trim();
    }
    else{
        chatId = await getChatId();
        if(chatId === null){
            console.log(`${chatName} Chat NOT found exiting immediately...`);
            process.exit(1);
        }
        fs.writeFileSync(path.resolve(__dirname,"chatId.txt"),chatId);
    }
    assignments = getPendingSubmission();
});


//the main juice
//fetch updated list of pending assignments
//parse and send reminders accordinly 
fs.watchFile(filepath,{interval: 1000},(curr, prev) =>{
    if(curr.mtime.getTime() !== prev.mtime.getTime()){
        try{
            let data = JSON.parse(fs.readFileSync(filepath,"utf8"));
            
            //update global pending assignments
            assignments = data;

            //parse data
            //when new exp added msg "Assignment details due on blah"
            //check 24, 12 and 1 hr mark for all assingments add due mssgs accordingly
            removeReminders(data);
            setReminders(data);

        }catch(err){
            console.error("Json read failed",err);
        }
    }
});




client.initialize();

//to terminate session 'ctrl + c' or when java program forces shutdown
process.on('SIGINT', async () => {
    console.log("Caught SIGINT, closing session...");
    await client.destroy();
    process.exit(0);
});

process.on('SIGTERM', async () => {
    console.log("Caught SIGTERM, closing session...");
    await client.destroy();
    process.exit(0);
});


//------------------- HELPER FUNCTIONS -------------------------
//fetch chatId of chatName
async function getChatId(){
    const chats = await client.getChats();
    for(let i = 0; i < chats.length; i++){
        if(chats[i].name === chatName){
            console.log(`${chatName} Chat Found!!`);
            return chats[i].id._serialized;;
        }
    }
    return null;
}


//on startup fetch existing list of assigments pending
function getPendingSubmission(){
    try{
        const data = fs.readFileSync(filepath);
        const json = JSON.parse(data);
        setReminders(json);
        return json;
    }catch (err){
        console.error(err);
    }
    return null;
}


//add dhat ass to the list 
function setReminders(assignments){
    assignments.forEach((ass) => {
        const key = `${ass.experimentName} ${ass.subjectName}`.trim().toLowerCase();

        if (!reminders.has(key)){
            const due = new Date(`${ass.submissionDate}T${ass.submissionTime}`);
            const now = new Date();

            
            //send mssg for new Assignment added in the list of ever never ending pending assignments
            sendMessage(`${ass.experimentName} ${ass.subjectName} due on ${ass.submissionDate} ${ass.submissionTime}`);

            const timers = [];

            [24,12,1].forEach((hourmark) => {
                const diff = due.getTime() - now.getTime() - hourmark * 60 * 60 * 1000;
                if(diff > 0){
                    timers.push(setTimeout(() => sendMessage(`${hourmark}h reminder ${ass.experimentName} due soon!`), diff));
                }
            });

            reminders.set(key,timers);
        }
    });
}


//when assigment is deleted from pending list for whatever reason
function removeReminders(assignments){
    //get set of all keys from assignments
    const keys = new Set(assignments.map(ass => `${ass.experimentName} ${ass.subjectName}`.trim().toLowerCase()));

    //iterate over reminders and remove any key not present in keys
    for(let [key, timers] of reminders){
        if(!keys.has(key)){
            //clearTimeout for reminder funcition and delete key from reminders
            timers.forEach(clearTimeout);
            reminders.delete(key);
        }
    }
}



//send mssg to chatName grp for exp reminder
async function sendMessage(message){
    if(!chatId){
        console.error("No chatId available, skipping:", message);
        return;
    }

    try {
        await client.sendMessage(chatId, message);
    } catch(err){
        console.error("Failed to send message:", err);

        // try refreshing chatId incase name changed or whatever teh reason maybe
        try {
            chatId = await getChatId();
            if(chatId) fs.writeFileSync(path.resolve(__dirname,"chatId.txt"), chatId);
            console.log("chatId refreshed, retrying message...");
            await client.sendMessage(chatId, message);
        } catch(innerErr) {
            console.error("Retry failed:", innerErr);
        }
    }
}
