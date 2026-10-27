package com.ticketbooking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification extends BaseEntity {
    private String fanId;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public Notification() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String id, String fanId, String message) {
        this(id, fanId, message, LocalDateTime.now(), false);
    }

    public Notification(String id, String fanId, String message, LocalDateTime createdAt,
            boolean read) {
        super(id);
        this.fanId = fanId;
        this.message = message;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.read = read;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toCsvLine() {
        return String.join(",", csvField(getId()), csvField(fanId), csvField(message),
                csvField(createdAt == null ? "" : createdAt.toString()), Boolean.toString(read));
    }

    public Notification fromCsvLine(String line) {
        List<String> values = parseCsvLine(line);
        if (values.size() != 5) {
            throw new IllegalArgumentException("Invalid Notification CSV data");
        }
        if (!"true".equalsIgnoreCase(values.get(4))
                && !"false".equalsIgnoreCase(values.get(4))) {
            throw new IllegalArgumentException("Invalid notification read status");
        }

        String timestamp = values.get(3);
        return new Notification(values.get(0), values.get(1), values.get(2),
                timestamp.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(timestamp),
                Boolean.parseBoolean(values.get(4)));
    }

    public String csvField(String value) {
        String safeValue = value == null ? "" : value.replace("\\", "\\\\")
                .replace("\r", "\\r").replace("\n", "\\n");
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    public List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                values.add(unescapeLineBreaks(value.toString()));
                value.setLength(0);
            } else {
                value.append(current);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Invalid Notification CSV quoting");
        }
        values.add(unescapeLineBreaks(value.toString()));
        return values;
    }

    public String unescapeLineBreaks(String value) {
        StringBuilder unescaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' && i + 1 < value.length()) {
                char escaped = value.charAt(i + 1);
                if (escaped == '\\') {
                    unescaped.append('\\');
                    i++;
                    continue;
                }
                if (escaped == 'n') {
                    unescaped.append('\n');
                    i++;
                    continue;
                }
                if (escaped == 'r') {
                    unescaped.append('\r');
                    i++;
                    continue;
                }
            }
            unescaped.append(current);
        }
        return unescaped.toString();
    }
}