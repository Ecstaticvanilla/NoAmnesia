'''
Make sure that moodle assignments get autocollected from moodle login
'''

import requests
from bs4 import BeautifulSoup
import json
from datetime import datetime
import os
import time

PENDING_FILE = "Data/Pending.json"
if os.path.exists(PENDING_FILE):
    with open(PENDING_FILE, "r", encoding="utf-8") as f:
        pending_events = json.load(f)
else:
    pending_events = []

# Helper: Check if event already exists by name
def ispending(name):
    return any(e["experimentName"] == name for e in pending_events)

def scrape (USERNAME,PASSWORD):
    MOODLE_URL = "https://moodle.spit.ac.in"

    session = requests.Session()

    #login page
    loginurl = session.get(f"{MOODLE_URL}/login/index.php", verify=False)
    #Initial Soup for scraping
    soup = BeautifulSoup(loginurl.text, "html.parser")
    logintoken = soup.find("input", {"name": "logintoken"}) 
    token = logintoken["value"] if logintoken else ""
    payload = {
        "username": USERNAME,
        "password": PASSWORD,
        "logintoken": token
    }
    resp = session.post(f"{MOODLE_URL}/login/index.php", data=payload, verify=False)
    if "login" in resp.url:
        print("Login failed – check username/password")
        exit()
    print("Login successful")

    calendar_page = session.get(f"{MOODLE_URL}/calendar/view.php?view=month", verify=False)
    soup = BeautifulSoup(calendar_page.text, "html.parser")

    for a in soup.select("td.day.hasevent li[data-region='event-item'] a[data-action='view-event']"):
        name = a.get("title")
        link = a["href"]
        if ispending(name):
            continue
        detailpage = session.get(link, verify=False)
        detailsoup = BeautifulSoup(detailpage.text, "html.parser")

        datesdiv = detailsoup.select_one("div.activity-dates .description-inner")
        opened = due = None
        duedate = duetime = None
        if datesdiv:
            for div in datesdiv.find_all("div"):
                if "Opened:" in div.text:
                    opened = div.text.replace("Opened:", "").strip()
                elif "Due:" in div.text:
                    due = div.text.replace("Due:", "").strip()
                    try:
                        dt = datetime.strptime(due, "%A, %d %B %Y, %I:%M %p")
                        duedate = dt.strftime("%Y-%m-%d")
                        duetime = dt.strftime("%H:%M:%S")
                    except:
                        duedate = due
                        duetime = None

            pending_events.append({"experimentName": name, "subjectName": link, "submissionDate": duedate, "submissionTime": duetime})

    print(json.dumps(pending_events, indent=2, ensure_ascii=False))
    with open(PENDING_FILE, "w", encoding="utf-8") as f:
        json.dump(pending_events, f, indent=2, ensure_ascii=False)

    time.sleep(0.5)



