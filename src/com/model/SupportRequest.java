package com.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.model.enums.SupportRequestStatus;
import com.util.CsvParser;

public class SupportRequest extends SimpleEntity {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String requestId;
    private String fanId;
    private String staffId;
    private String subject;
    private String message;
    private String response;
    private SupportRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public SupportRequest() {
        this.status = SupportRequestStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public SupportRequest(String requestId, String fanId, String staffId, String subject,
                          String message, String response, SupportRequestStatus status,
                          LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.requestId = requestId;
        this.fanId = fanId;
        this.staffId = staffId == null ? "" : staffId;
        this.subject = subject;
        this.message = message;
        this.response = response == null ? "" : response;
        this.status = status == null ? SupportRequestStatus.OPEN : status;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.resolvedAt = resolvedAt;
    }

    @Override
    public String getId() {
        return requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SupportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @Override
    public String toCsvLine() {
        return CsvParser.encode(
                requestId,
                fanId,
                staffId == null ? "" : staffId,
                subject == null ? "" : subject,
                message == null ? "" : message,
                response == null ? "" : response,
                status == null ? SupportRequestStatus.OPEN.name() : status.name(),
                createdAt == null ? "" : createdAt.toString(),
                resolvedAt == null ? "" : resolvedAt.toString()
        );
    }

    @Override
    public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() < 9) {
            return;
        }
        this.requestId = values.get(0);
        this.fanId = values.get(1);
        this.staffId = values.get(2);
        this.subject = values.get(3);
        this.message = values.get(4);
        this.response = values.get(5);
        this.status = SupportRequestStatus.valueOf(values.get(6));
        this.createdAt = values.get(7).isEmpty() ? null : LocalDateTime.parse(values.get(7));
        this.resolvedAt = values.get(8).isEmpty() ? null : LocalDateTime.parse(values.get(8));
    }

    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("  Request ID  : %s [%s]%n", requestId, status));
        sb.append(String.format("  Fan ID      : %s%n", fanId));
        sb.append(String.format("  Created At  : %s%n", createdAt == null ? "N/A" : createdAt.format(FMT)));
        sb.append(String.format("  Subject     : %s%n", subject));
        sb.append(String.format("  Message     : %s%n", message));
        if (status != SupportRequestStatus.OPEN) {
            sb.append(String.format("  Staff In Charge : %s%n", (staffId == null || staffId.isEmpty()) ? "N/A" : staffId));
            sb.append(String.format("  Resolved At : %s%n", resolvedAt == null ? "N/A" : resolvedAt.format(FMT)));
            sb.append(String.format("  Response    : %s%n", (response == null || response.isEmpty()) ? "N/A" : response));
        }
        sb.append("------------------------------------------------------------\n");
        return sb.toString();
    }
}
