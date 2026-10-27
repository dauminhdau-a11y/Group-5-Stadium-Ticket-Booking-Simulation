
package com.model;

import com.model.interfaces.BaseEntity;
import java.util.UUID;

public class SimulationResult implements BaseEntity {

    private static final String CSV_VERSION = "SIMULATION_RESULT_V2";

    private String resultId;
    private SyncMechanism mechanism;
    private int threadCount;
    private long durationMs;
    private int successfulAttempts;
    private int rejectedAttempts;
    private int doubleBookedSeats;
    private int workerErrorCount;
    private int legacyDuplicateAttempts;
    private String legacyCsvLine;

    // Constructor không tham số
    public SimulationResult() {
        this.resultId = UUID.randomUUID().toString();
    }

    // Constructor đầy đủ thông tin kết quả
    public SimulationResult(
            SyncMechanism mechanism,
            int threadCount,
            long durationMs,
            int successfulAttempts,
            int rejectedAttempts,
            int doubleBookedSeats,
            int workerErrorCount) {

        this();

        this.mechanism = mechanism;
        this.threadCount = threadCount;
        this.durationMs = durationMs;
        this.successfulAttempts = successfulAttempts;
        this.rejectedAttempts = rejectedAttempts;
        this.doubleBookedSeats = doubleBookedSeats;
        this.workerErrorCount = workerErrorCount;
    }

    public String getResultId() {
        return resultId;
    }

    public SyncMechanism getMechanism() {
        return mechanism;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getTotalThreads() {
        return threadCount;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getSuccessCount() {
        return getSuccessfulAttempts();
    }

    public int getFailCount() {
        return getRejectedAttempts();
    }

    public int getDoubleBookingCount() {
        return getDoubleBookedSeats();
    }

    public int getSuccessfulAttempts() {
        return successfulAttempts;
    }

    public int getRejectedAttempts() {
        return rejectedAttempts;
    }

    public int getDoubleBookedSeats() {
        return doubleBookedSeats;
    }

    public int getWorkerErrorCount() {
        return workerErrorCount;
    }

    public boolean hasWorkerErrors() {
        return workerErrorCount > 0;
    }

    public int getLegacyDuplicateAttempts() {
        return legacyDuplicateAttempts;
    }

    // Số lượt đặt thành công trung bình mỗi giây
    public double getTps() {
        if (durationMs <= 0) {
            return 0.0;
        }

        return successfulAttempts * 1000.0 / durationMs;
    }

    public double getThroughputPs() {
        return getTps();
    }

    @Override
    public String getId() {
        return resultId;
    }

    @Override
    public String toCsvLine() {
        if (legacyCsvLine != null) {
            return legacyCsvLine;
        }

        return CSV_VERSION + ","
                + resultId + ","
                + mechanism + ","
                + threadCount + ","
                + durationMs + ","
                + successfulAttempts + ","
                + rejectedAttempts + ","
                + doubleBookedSeats + ","
                + workerErrorCount;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] values = line.split(",", -1);

        if (values.length == 7) {
            resultId = values[0];
            mechanism = SyncMechanism.valueOf(values[1]);
            threadCount = Integer.parseInt(values[2]);
            durationMs = Long.parseLong(values[3]);
            successfulAttempts = Integer.parseInt(values[4]);
            rejectedAttempts = Integer.parseInt(values[5]);
            legacyDuplicateAttempts = Integer.parseInt(values[6]);
            doubleBookedSeats = 0;
            workerErrorCount = 0;
            legacyCsvLine = line;
            return;
        }

        if (values.length != 9 || !CSV_VERSION.equals(values[0])) {
            throw new IllegalArgumentException(
                    "Invalid SimulationResult CSV row");
        }

        resultId = values[1];
        mechanism = SyncMechanism.valueOf(values[2]);
        threadCount = Integer.parseInt(values[3]);
        durationMs = Long.parseLong(values[4]);
        successfulAttempts = Integer.parseInt(values[5]);
        rejectedAttempts = Integer.parseInt(values[6]);
        doubleBookedSeats = Integer.parseInt(values[7]);
        workerErrorCount = Integer.parseInt(values[8]);
        legacyDuplicateAttempts = 0;
        legacyCsvLine = null;
    }
}
