/*
* Main Launcher Class
* Launch Form.java to open the Window
* Launch FileStorage.java to load assignments and deadlines from Data Folder
* Start Notification.java in background
*/
public class Main{
    public static void main(String[] args) {
        //Run Main Form
        // ui.Form form = new ui.Form();
        javax.swing.SwingUtilities.invokeLater(() -> new ui.Form());  
        background.Notification notif = new background.Notification();
        notif.startScheduler();
    } 
}