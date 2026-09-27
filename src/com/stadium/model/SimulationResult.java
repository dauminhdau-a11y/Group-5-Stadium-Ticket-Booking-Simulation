package com.stadium.model;

public class SimulationResult {

    private final int totalUsers;
    private final int totalAttempts;
    private final int successfulBookings;
    private final int failedBookings;
    private final int totalSeats;
    private final long executionTime;

    public SimulationResult(
            int totalUsers,
            int totalAttempts,
            int successfulBookings,
            int failedBookings,
            int totalSeats,
            long executionTime) {

        this.totalUsers = totalUsers;
        this.totalAttempts = totalAttempts;
        this.successfulBookings = successfulBookings;
        this.failedBookings = failedBookings;
        this.totalSeats = totalSeats;
        this.executionTime = executionTime;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getSuccessfulBookings() {
        return successfulBookings;
    }

    public int getFailedBookings() {
        return failedBookings;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public double getSuccessRate() {

        if (totalAttempts == 0) {
            return 0;
        }

        return successfulBookings * 100.0 / totalAttempts;
    }

    public double getFailureRate() {

        if (totalAttempts == 0) {
            return 0;
        }

        return failedBookings * 100.0 / totalAttempts;
    }
}