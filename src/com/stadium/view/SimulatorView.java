
package com.stadium.view;

import com.stadium.controller.SimulatorController;
import com.stadium.model.SimulationResult;
import com.stadium.model.SyncMechanism;

import java.util.Scanner;

public class SimulatorView {

    private final Scanner scanner;
    private final SimulatorController controller;

    public SimulatorView() {
        this(new Scanner(System.in),
                new SimulatorController());
    }

    public SimulatorView(
            Scanner scanner,
            SimulatorController controller) {

        this.scanner = scanner;
        this.controller = controller;
    }

    public void showSimulator() {
        System.out.println(
                "\n=== STADIUM BOOKING SIMULATOR ===");

        int users = readPositiveInt(
                "Number of concurrent users: ");

        System.out.println(
                "Synchronization mechanisms:");

        SyncMechanism[] mechanisms =
                SyncMechanism.values();

        for (int i = 0; i < mechanisms.length; i++) {
            System.out.printf(
                    "%d. %s%n", i + 1, mechanisms[i]);
        }

        int choice = readRange(
                "Choose mechanism: ",
                1,
                mechanisms.length);

        SimulationResult result =
                controller.runSimulation(
                        users, mechanisms[choice - 1]);

        printSimulationResult(result);

        System.out.print(
                "Run another simulation? (y/n): ");

        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("y")) {
            showSimulator();
        } else {
            System.out.println("Simulation ended.");
        }
    }

    private int readPositiveInt(String prompt) {
        return readRange(prompt, 1, 10000);
    }

    private int readRange(
            String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);

            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);

                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Yêu cầu người dùng nhập lại
            }

            System.out.printf(
                    "Enter an integer from %d to %d.%n",
                    min, max);
        }
    }

    public void printSimulationResult(
            SimulationResult result) {

        System.out.println(
                "\n========== SIMULATION RESULT ==========");

        System.out.println(
                "Mechanism            : "
                + result.getMechanism());

        System.out.println(
                "Concurrent users     : "
                + result.getTotalThreads());

        System.out.println(
                "Successful bookings  : "
                + result.getSuccessCount());

        System.out.println(
                "Failed attempts      : "
                + result.getFailCount());

        System.out.println(
                "Duplicate attempts   : "
                + result.getDoubleBookingCount());

        System.out.printf(
                "Duplicate rate       : %.2f%%%n",
                result.getDoubleBookingRate());

        System.out.println(
                "Duration             : "
                + result.getDurationMs() + " ms");

        System.out.printf(
                "Throughput           : %.2f bookings/sec%n",
                result.getThroughputPs());

        System.out.println(
                "=======================================");
    }

    public void drawComparisonChart() {
        System.out.println(
                "Comparison chart requires results "
                + "from multiple simulation runs.");
    }
}
