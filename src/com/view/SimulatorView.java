package com.view;

import com.controller.SimulatorController;
import com.model.SimulationResult;
import com.model.SyncMechanism;

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
        while (true) {
            System.out.println(
                    "\n=== STADIUM BOOKING SIMULATOR ===");

            Integer users = readPositiveInt(
                    "Number of concurrent users: ");
            if (users == null) {
                break;
            }

            System.out.println(
                    "Synchronization mechanisms:");

            SyncMechanism[] mechanisms =
                    SyncMechanism.values();

            for (int i = 0; i < mechanisms.length; i++) {
                System.out.printf(
                        "%d. %s%n", i + 1, mechanisms[i]);
            }

            Integer choice = readRange(
                    "Choose mechanism: ",
                    1,
                    mechanisms.length);
            if (choice == null) {
                break;
            }

            SimulationResult result =
                    controller.runSimulation(
                            users, mechanisms[choice - 1]);

            printSimulationResult(result);

            System.out.print(
                    "Run another simulation? (y/n): ");
            if (!scanner.hasNextLine()) {
                break;
            }

            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                break;
            }
        }

        System.out.println("Simulation ended.");
    }

    private Integer readPositiveInt(String prompt) {
        return readRange(prompt, 1, 10000);
    }

    private Integer readRange(
            String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return null;
            }

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
                "Successful attempts  : "
                + result.getSuccessfulAttempts());

        System.out.println(
                "Rejected attempts    : "
                + result.getRejectedAttempts());

        System.out.println(
                "Double-booked seats  : "
                + result.getDoubleBookedSeats());

        System.out.println(
                "Worker errors        : "
                + result.getWorkerErrorCount());

        System.out.println(
                "Run status           : "
                + (result.hasWorkerErrors()
                        ? "ERROR"
                        : "COMPLETED"));

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
