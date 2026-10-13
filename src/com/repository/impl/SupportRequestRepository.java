package com.repository.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.model.SupportRequest;
import com.model.enums.SupportRequestStatus;

public class SupportRequestRepository extends AbstractCsvRepository<SupportRequest> {
    public SupportRequestRepository() {
        super(SupportRequest.class, "support_requests.csv");
    }

    public SupportRequestRepository(Path dataDirectory) {
        super(SupportRequest.class, dataDirectory.resolve("support_requests.csv"));
    }

    public List<SupportRequest> findByStatus(SupportRequestStatus status) {
        List<SupportRequest> result = new ArrayList<SupportRequest>();
        for (SupportRequest req : findAll()) {
            if (req.getStatus() == status) {
                result.add(req);
            }
        }
        return result;
    }

    public List<SupportRequest> findByFanId(String fanId) {
        List<SupportRequest> result = new ArrayList<SupportRequest>();
        for (SupportRequest req : findAll()) {
            if (fanId != null && fanId.equalsIgnoreCase(req.getFanId())) {
                result.add(req);
            }
        }
        return result;
    }
}
