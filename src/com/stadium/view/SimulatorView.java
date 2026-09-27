package com.stadium.view;

import com.stadium.controller.SimulatorController;
import com.stadium.model.SimulationResult;

import java.util.Scanner;

public class SimulatorView {

    private final Scanner scanner;
    private final SimulatorController controller;

    public SimulatorView() {

        scanner = new Scanner(System.in);
        controller = new SimulatorController();
    }

    public void showSimulator() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("       STADIUM BOOKING SIMULATOR");
        System.out.println("========================================");

        System.out.print("Enter number of users: ");
        int users = scanner.nextInt();

        System.out.print("Enter number of seats: ");
        int seats = scanner.nextInt();

        try {

            SimulationResult result =
                    controller.runSimulation(
                            users,
                            seats
                    );

            displayResult(result);

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }

    private void displayResult(
            SimulationResult result) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          SIMULATION RESULT");
        System.out.println("========================================");

        System.out.println(
                "Total users       : "
                + result.getTotalUsers()
        );

        System.out.println(
                "Total attempts    : "
                + result.getTotalAttempts()
        );

        System.out.println(
                "Total seats       : "
                + result.getTotalSeats()
        );

        System.out.println(
                "Successful        : "
                + result.getSuccessfulBookings()
        );

        System.out.println(
                "Failed            : "
                + result.getFailedBookings()
        );

        System.out.printf(
                "Success rate      : %.2f%%%n",
                result.getSuccessRate()
        );

        System.out.printf(
                "Failure rate      : %.2f%%%n",
                result.getFailureRate()
        );

        System.out.println(
                "Execution time    : "
                + result.getExecutionTime()
                + " ms"
        );

        System.out.println("========================================");
    }
}