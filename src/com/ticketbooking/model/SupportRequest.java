package com.ticketbooking.model;

import com.ticketbooking.model.enums.SupportStatus;
import java.time.LocalDateTime;

public class SupportRequest extends BaseEntity {
    private String requestId;
    private String fanId;
    private String subject;
    private String content;
    private SupportStatus status;
    private String response;
    private LocalDateTime createdAt;

    public SupportRequest() {
        super();
        this.status = SupportStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.response = "";
    }

    public SupportRequest(String requestId, String fanId, String subject, String content) {
        this(requestId, fanId, subject, content, SupportStatus.OPEN, "", LocalDateTime.now());
    }

    public SupportRequest(String requestId, String fanId, String subject, String content,
            SupportStatus status, String response, LocalDateTime createdAt) {
        super(requestId);
        this.requestId = requestId;
        this.fanId = fanId;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.response = response != null ? response : "";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId != null ? requestId : getId();
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
        setId(requestId);
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SupportStatus getStatus() {
        return status != null ? status : SupportStatus.OPEN;
    }

    public void setStatus(SupportStatus status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toCsvLine() {
        String safeContent = content != null ? content.replace(",", ";") : "";
        String safeResponse = response != null ? response.replace(",", ";") : "";
        String safeSubject = subject != null ? subject.replace(",", ";") : "";
        return String.join(",", getRequestId(),
                fanId != null ? fanId : "",
                safeSubject,
                safeContent,
                status != null ? status.name() : SupportStatus.OPEN.name(),
                safeResponse,
                createdAt != null ? createdAt.toString() : "");
    }
}
