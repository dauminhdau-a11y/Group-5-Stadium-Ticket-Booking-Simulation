
package com.stadium.model;

import com.stadium.model.interfaces.BaseEntity;
import java.util.UUID;

public class SimulationResult implements BaseEntity {

    private String resultId;
    private SyncMechanism mechanism;
    private int threadCount;
    private long durationMs;
    private int successCount;
    private int failCount;
    private int doubleBookingCount;

    // Constructor không tham số
    public SimulationResult() {
        this.resultId = UUID.randomUUID().toString();
    }

    // Constructor đầy đủ thông tin kết quả
    public SimulationResult(
            SyncMechanism mechanism,
            int threadCount,
            long durationMs,
            int successCount,
            int failCount,
            int doubleBookingCount) {

        this();

        this.mechanism = mechanism;
        this.threadCount = threadCount;
        this.durationMs = durationMs;
        this.successCount = successCount;
        this.failCount = failCount;
        this.doubleBookingCount = doubleBookingCount;
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
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getDoubleBookingCount() {
        return doubleBookingCount;
    }

    // Số lượt đặt thành công trung bình mỗi giây
    public double getTps() {
        if (durationMs <= 0) {
            return 0.0;
        }

        return successCount * 1000.0 / durationMs;
    }

    public double getThroughputPs() {
        return getTps();
    }

    // Tỷ lệ thử đặt trùng trên tổng số người dùng
    public double getDoubleBookingRate() {
        if (threadCount <= 0) {
            return 0.0;
        }

        return doubleBookingCount * 100.0 / threadCount;
    }

    @Override
    public String getId() {
        return resultId;
    }

    @Override
    public String toCsvLine() {
        return resultId + ","
                + mechanism + ","
                + threadCount + ","
                + durationMs + ","
                + successCount + ","
                + failCount + ","
                + doubleBookingCount;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] values = line.split(",", -1);

        if (values.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid SimulationResult CSV row");
        }

        resultId = values[0];
        mechanism = SyncMechanism.valueOf(values[1]);
        threadCount = Integer.parseInt(values[2]);
        durationMs = Long.parseLong(values[3]);
        successCount = Integer.parseInt(values[4]);
        failCount = Integer.parseInt(values[5]);
        doubleBookingCount = Integer.parseInt(values[6]);
    }
}
