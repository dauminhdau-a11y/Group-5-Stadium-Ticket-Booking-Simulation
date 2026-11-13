package view;

import controller.NotificationController;
import model.Notification;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class NotificationView {
    private NotificationController notificationController;
    private Scanner scanner;

    public NotificationView(NotificationController notificationController, Scanner scanner) {
        if (notificationController == null || scanner == null) {
            throw new IllegalArgumentException("Notification controller and scanner must not be null.");
        }
        this.notificationController = notificationController;
        this.scanner = scanner;
    }

    public void setScanner(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner must not be null.");
        }
        this.scanner = scanner;
    }

    public void showForFan(String fanId) throws IOException {
        List<Notification> notifications = notificationController.getNotificationsByFan(fanId);
        if (notifications.isEmpty()) {
            System.out.println("You have no notifications.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("\n=== YOUR NOTIFICATIONS (UNREAD: "
                + notificationController.getUnreadCount(fanId) + ") ===");
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            System.out.printf("%d. [%s] %s | %s%n", i + 1,
                    notification.isRead() ? "READ" : "UNREAD",
                    notification.getCreatedAt().format(formatter), notification.getMessage());
        }
        System.out.print("Enter notification number to mark as read, A to mark all as read, or Enter to return: ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : null;
        if (choice == null || choice.isEmpty()) {
            return;
        }
        if ("A".equalsIgnoreCase(choice)) {
            int markedCount = notificationController.markAllAsRead(fanId);
            System.out.println(markedCount + " notification(s) marked as read.");
            return;
        }
        try {
            int selected = Integer.parseInt(choice);
            if (selected < 1 || selected > notifications.size()) {
                System.out.println("Notification number is out of range.");
                return;
            }
            Notification notification = notifications.get(selected - 1);
            if (notificationController.markAsRead(fanId, notification.getId())) {
                System.out.println("Notification marked as read.");
            } else {
                System.out.println("Unable to mark the notification as read.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid notification selection.");
        }
    }
}
