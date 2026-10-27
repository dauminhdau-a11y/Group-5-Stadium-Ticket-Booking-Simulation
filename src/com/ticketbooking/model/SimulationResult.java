package com.ticketbooking.model;

import com.ticketbooking.model.enums.SyncMechanism;
import java.time.LocalDateTime;

public class SimulationResult extends BaseEntity {
    private String resultId;
    private SyncMechanism mechanism;
    private int threadCount;
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private long executionTimeMs;
    private LocalDateTime timestamp;

    public SimulationResult() {
        super();
        this.timestamp = LocalDateTime.now();
    }

    public SimulationResult(String resultId, SyncMechanism mechanism, int threadCount,
            int totalRequests, int successfulRequests, int failedRequests,
            long executionTimeMs, LocalDateTime timestamp) {
        super(resultId);
        this.resultId = resultId;
        this.mechanism = mechanism;
        this.threadCount = threadCount;
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.executionTimeMs = executionTimeMs;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getResultId() {
        return resultId != null ? resultId : getId();
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
        setId(resultId);
    }

    public SyncMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(SyncMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public void setSuccessfulRequests(int successfulRequests) {
        this.successfulRequests = successfulRequests;
    }

    public int getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(int failedRequests) {
        this.failedRequests = failedRequests;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toCsvLine() {
        return String.join(",", getResultId(),
                mechanism != null ? mechanism.name() : SyncMechanism.NO_LOCK.name(),
                Integer.toString(threadCount),
                Integer.toString(totalRequests),
                Integer.toString(successfulRequests),
                Integer.toString(failedRequests),
                Long.toString(executionTimeMs),
                timestamp != null ? timestamp.toString() : "");
    }
}
