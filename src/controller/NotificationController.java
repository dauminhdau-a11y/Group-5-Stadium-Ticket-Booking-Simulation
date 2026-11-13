package controller;

import model.Notification;
import model.Transaction;
import repository.NotificationRepository;
import java.io.IOException;
import java.util.List;

public class NotificationController {
    private NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        if (notificationRepository == null) {
            throw new IllegalArgumentException("Notification repository must not be null.");
        }
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotificationsByFan(String fanId) throws IOException {
        return notificationRepository.findByFanId(fanId);
    }

    public int getUnreadCount(String fanId) throws IOException {
        return notificationRepository.findUnreadByFanId(fanId).size();
    }

    public boolean markAsRead(String fanId, String notificationId) throws IOException {
        for (Notification notification : notificationRepository.findByFanId(fanId)) {
            if (notification.getId().equals(notificationId)) {
                return notificationRepository.markAsRead(notificationId);
            }
        }
        return false;
    }

    public int markAllAsRead(String fanId) throws IOException {
        return notificationRepository.markAllAsRead(fanId);
    }

    public void addBookingConfirmedNotification(Transaction transaction) throws IOException {
        if (transaction == null || transaction.getTransactionId() == null
                || transaction.getTransactionId().trim().isEmpty()
                || transaction.getFanId() == null || transaction.getFanId().trim().isEmpty()) {
            return;
        }
        String notificationId = "BOOKING-CONFIRMED-" + transaction.getTransactionId();
        if (notificationRepository.findById(notificationId) != null) {
            return;
        }
        String message = "Booking " + transaction.getTransactionId()
                + " has been confirmed. Match: " + transaction.getMatchId()
                + "; seats: " + String.join(", ", transaction.getSeatIds()) + ".";
        notificationRepository.save(new Notification(notificationId, transaction.getFanId(), message));
    }
}
