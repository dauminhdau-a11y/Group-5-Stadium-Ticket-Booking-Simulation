package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.model.enums.NotificationType;
import com.util.CsvParser;

public class Notification extends SimpleEntity {
    private String notificationId;
    private String userId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String notificationId, String userId, NotificationType type, String message, LocalDateTime createdAt, boolean read) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.read = read;
    }

    @Override public String getId() { return notificationId; }
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Override public String toCsvLine() {
        return CsvParser.encode(notificationId, userId, type == null ? "" : type.name(), message,
                createdAt == null ? "" : createdAt.toString(), Boolean.toString(read));
    }

    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() < 6) return;
        notificationId = values.get(0);
        userId = values.get(1);
        type = NotificationType.valueOf(values.get(2));
        message = values.get(3);
        createdAt = LocalDateTime.parse(values.get(4));
        read = Boolean.parseBoolean(values.get(5));
    }
}
