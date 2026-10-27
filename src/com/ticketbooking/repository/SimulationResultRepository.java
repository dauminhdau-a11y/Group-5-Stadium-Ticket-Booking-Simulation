package com.ticketbooking.repository;

import com.ticketbooking.model.SimulationResult;
import com.ticketbooking.model.enums.SyncMechanism;
import java.time.LocalDateTime;

public class SimulationResultRepository extends CsvRepository<SimulationResult> {
    public SimulationResultRepository() {
        this("data/simulation_results.csv");
    }

    public SimulationResultRepository(String fileName) {
        super(fileName);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "resultId,mechanism,threadCount,totalRequests,successfulRequests,failedRequests,executionTimeMs,timestamp"
                .equalsIgnoreCase(line.trim());
    }

    @Override
    public SimulationResult parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            String id = parts[0].trim();
            SyncMechanism mechanism = SyncMechanism.valueOf(parts[1].trim());
            int threadCount = Integer.parseInt(parts[2].trim());
            int total = Integer.parseInt(parts[3].trim());
            int success = Integer.parseInt(parts[4].trim());
            int failed = Integer.parseInt(parts[5].trim());
            long time = Long.parseLong(parts[6].trim());
            LocalDateTime ts = parts.length >= 8 && !parts[7].trim().isEmpty()
                    ? LocalDateTime.parse(parts[7].trim()) : LocalDateTime.now();
            return new SimulationResult(id, mechanism, threadCount, total, success, failed, time, ts);
        }
        return new SimulationResult(parts[0].trim(), SyncMechanism.NO_LOCK, 1, 0, 0, 0, 0, LocalDateTime.now());
    }
}
