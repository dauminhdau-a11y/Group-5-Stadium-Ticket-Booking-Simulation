package com.ticketbooking.repository;

import com.ticketbooking.model.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NotificationRepository extends CsvRepository<Notification> {
    public NotificationRepository() {
        this("data/notifications.csv");
    }

    public NotificationRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Notification parseLine(String line) {
        return new Notification().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "id,fanId,message,createdAt,read".equalsIgnoreCase(line.trim());
    }

    public List<Notification> findByFanId(String fanId) throws IOException {
        List<Notification> notifications = new ArrayList<>();
        if (fanId == null || fanId.trim().isEmpty()) {
            return notifications;
        }

        String normalizedFanId = fanId.trim();
        for (Notification notification : findAll()) {
            if (notification.getFanId() != null
                    && notification.getFanId().equalsIgnoreCase(normalizedFanId)) {
                notifications.add(notification);
            }
        }
        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return notifications;
    }

    public List<Notification> findUnreadByFanId(String fanId) throws IOException {
        List<Notification> unreadNotifications = new ArrayList<>();
        for (Notification notification : findByFanId(fanId)) {
            if (!notification.isRead()) {
                unreadNotifications.add(notification);
            }
        }
        return unreadNotifications;
    }

    public boolean markAsRead(String notificationId) throws IOException {
        if (notificationId == null || notificationId.trim().isEmpty()) {
            return false;
        }

        Optional<Notification> result = findAll().stream()
                .filter(notification -> notificationId.equals(notification.getId()))
                .findFirst();
        if (!result.isPresent()) {
            return false;
        }

        Notification notification = result.get();
        if (!notification.isRead()) {
            notification.markAsRead();
            save(notification);
        }
        return true;
    }

    public int markAllAsRead(String fanId) throws IOException {
        if (fanId == null || fanId.trim().isEmpty()) {
            return 0;
        }

        String normalizedFanId = fanId.trim();
        List<Notification> notifications = findAll();
        int updatedCount = 0;
        for (Notification notification : notifications) {
            if (notification.getFanId() != null
                    && notification.getFanId().equalsIgnoreCase(normalizedFanId)
                    && !notification.isRead()) {
                notification.markAsRead();
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
            writeAll(notifications);
        }
        return updatedCount;
    }
}