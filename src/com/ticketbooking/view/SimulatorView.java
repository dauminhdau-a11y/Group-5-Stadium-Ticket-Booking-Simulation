package com.ticketbooking.view;

import com.ticketbooking.controller.SimulatorController;
import com.ticketbooking.model.SimulationResult;
import java.util.List;

public class SimulatorView {
    private SimulatorController simulatorController;

    public SimulatorView(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
    }

    public SimulatorController getSimulatorController() {
        return simulatorController;
    }

    public void setSimulatorController(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
    }

    public void displayComparisonTable(List<SimulationResult> results) {
        System.out.println("\n================================= SIMULATION COMPARISON RESULTS =================================");
        if (results == null || results.isEmpty()) {
            System.out.println("No simulation results available.");
            return;
        }
        System.out.printf("%-12s | %-14s | %-8s | %-14s | %-10s | %-10s | %-10s%n",
                "Result ID", "Mechanism", "Threads", "Total Requests", "Success", "Failed", "Time (ms)");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (SimulationResult r : results) {
            System.out.printf("%-12s | %-14s | %-8d | %-14d | %-10d | %-10d | %-10d%n",
                    r.getResultId(),
                    r.getMechanism() != null ? r.getMechanism().name() : "N/A",
                    r.getThreadCount(),
                    r.getTotalRequests(),
                    r.getSuccessfulRequests(),
                    r.getFailedRequests(),
                    r.getExecutionTimeMs());
        }
        System.out.println("=================================================================================================");
    }
}
