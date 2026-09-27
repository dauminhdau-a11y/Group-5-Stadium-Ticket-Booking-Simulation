package com.stadium.controller;

import com.stadium.model.enums.SyncMechanism;

public class SimulationResult {
    private SyncMechanism mechanism;
    private int totalThreads;
    private int successCount;
    private int doubleBookingCount;
    private double throughputTps;

    public SimulationResult() { }
    public SimulationResult(SyncMechanism mechanism, int totalThreads, int successCount,
                            int doubleBookingCount, double throughputTps) {
        this.mechanism = mechanism; this.totalThreads = totalThreads; this.successCount = successCount;
        this.doubleBookingCount = doubleBookingCount; this.throughputTps = throughputTps;
    }
    public SyncMechanism getMechanism() { return mechanism; }
    public void setMechanism(SyncMechanism value) { mechanism = value; }
    public int getTotalThreads() { return totalThreads; }
    public void setTotalThreads(int value) { totalThreads = value; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int value) { successCount = value; }
    public int getDoubleBookingCount() { return doubleBookingCount; }
    public void setDoubleBookingCount(int value) { doubleBookingCount = value; }
    public double getThroughputTps() { return throughputTps; }
    public void setThroughputTps(double value) { throughputTps = value; }
}
