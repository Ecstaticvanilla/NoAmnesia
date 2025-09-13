# NoAmnesia

file structure
```text
NOAMNESIA
├── Data                 # runtime app data (not bundled, stays next to jar)
│   ├── Pending.json 
│   └── Submitted.json
|
├── lib                  # libraries
│   ├── moodle.py
│   └── org.json
│
├── out                  # compiled .class files (like your current "out/")
│   ├── backend
│   ├── background
│   ├── ui
│   └── Main.class
│
├── resources            # icons, images
│   ├── addicon.jpg
│   └── icon.png
│
├── src                  # all source code
│   ├── backend          # processes in backend
│   │   ├── AssignmentComponent.java
│   │   └── FileStorage.java
│   │
│   ├── background       # runs in background
│   │   ├── JsLauncher.java
│   │   └── Notification.java
│   │
│   └── ui               # Ui shows
│       └── Form.java
│
├── whatsapp_script      # Automessage sending script
│   └── index.js
|
├── .gitignore
├── NoAmnesia.jar
├── LICENSE
├── Main.java
├── ReadMe.md
└── setup.txt
