package com.ticketbooking.repository;

import com.ticketbooking.model.SupportRequest;
import com.ticketbooking.model.enums.SupportStatus;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SupportRequestRepository extends CsvRepository<SupportRequest> {
    public SupportRequestRepository() {
        this("data/support_requests.csv");
    }

    public SupportRequestRepository(String fileName) {
        super(fileName);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "requestId,fanId,subject,content,status,response,createdAt".equalsIgnoreCase(line.trim());
    }

    @Override
    public SupportRequest parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            String id = parts[0].trim();
            String fanId = parts[1].trim();
            String subject = parts[2].trim();
            String content = parts[3].trim();
            SupportStatus status = SupportStatus.valueOf(parts[4].trim());
            String response = parts[5].trim();
            LocalDateTime dt = parts[6].trim().isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(parts[6].trim());
            return new SupportRequest(id, fanId, subject, content, status, response, dt);
        } else if (parts.length >= 4) {
            String id = parts[0].trim();
            String fanId = parts[1].trim();
            String subject = parts[2].trim();
            String content = parts[3].trim();
            return new SupportRequest(id, fanId, subject, content);
        }
        return new SupportRequest(parts[0].trim(), "", "", "");
    }

    public List<SupportRequest> findByFanId(String fanId) throws IOException {
        List<SupportRequest> result = new ArrayList<>();
        if (fanId == null) {
            return result;
        }
        for (SupportRequest req : findAll()) {
            if (fanId.equalsIgnoreCase(req.getFanId())) {
                result.add(req);
            }
        }
        return result;
    }

    public List<SupportRequest> findByStatus(SupportStatus status) throws IOException {
        List<SupportRequest> result = new ArrayList<>();
        if (status == null) {
            return result;
        }
        for (SupportRequest req : findAll()) {
            if (req.getStatus() == status) {
                result.add(req);
            }
        }
        return result;
    }
}