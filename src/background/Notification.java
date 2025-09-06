/*
*Run a scheduled task every day.
*Check if today is deadline - 1 day.
*If yes → trigger notification via SystemTray. 
*/

package background;

import backend.AssignmentComponent;
import backend.FileStorage;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.awt.*;

public class Notification {

    private FileStorage fileStorage = new FileStorage();

    public Notification() {}

    public void checkDeadline() {
        List<AssignmentComponent> pendingList = fileStorage.retrievePendingAssignments();
        LocalDateTime now = LocalDateTime.now();

        for (AssignmentComponent a : pendingList) {
            Date aDate = a.getSubmissionDate();
            Time aTime = a.getSubmissionTime();

            LocalDateTime deadline = LocalDateTime.of(aDate.toLocalDate(), aTime.toLocalTime());
            Duration duration = Duration.between(now, deadline);

            long hoursLeft = duration.toHours();

            if (hoursLeft <= 24 && hoursLeft > 12) {
                showNotification(
                    "Upcoming Deadline!",
                    a.getExperimentName() + " (" + a.getSubjectName() + ") due in less than 24 hours."
                );
            } 
            else if (hoursLeft <= 12 && hoursLeft > 1) {
                showNotification(
                    "Upcoming Deadline!",
                    a.getExperimentName() + " (" + a.getSubjectName() + ") due in less than 12 hours."
                );
            } 
            else if (hoursLeft <= 1 && hoursLeft >= 0) {
                showNotification(
                    "Upcoming Deadline!",
                    a.getExperimentName() + " (" + a.getSubjectName() + ") due in less than 1 hour!"
                );
            }

        }
    }

    private void showNotification(String title, String message) {
        try {
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray not supported!");
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // put your app icon here
            TrayIcon trayIcon = new TrayIcon(image, "Assignment Reminder");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Assignment Notification");

            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

            // Remove after showing (to prevent multiple icons stacking)
            tray.remove(trayIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkDeadline, 0, 30, TimeUnit.SECONDS);
    }

}