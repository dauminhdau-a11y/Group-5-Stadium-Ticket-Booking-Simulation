package com.ticketbooking.view;

import com.ticketbooking.controller.FanController;
import com.ticketbooking.model.Fan;

import java.util.Scanner;

public class FanView {

    private final FanController fanController;
    private final Scanner scanner;

    public FanView(FanController fanController) {
        this.fanController = fanController;
        this.scanner = new Scanner(System.in);
    }

    public boolean register() {
        System.out.println("\n=== FAN REGISTER ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        boolean success = fanController.register(
                username,
                password,
                email
        );

        if (success) {
            System.out.println("Register successfully!");
        } else {
            System.out.println("Register failed. Username/email may already exist.");
        }

        return success;
    }

    public Fan login() {
        System.out.println("\n=== FAN LOGIN ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Fan fan = fanController.login(username, password);

        if (fan != null) {
            System.out.println("Login successfully!");
            displayProfile(fan);
        } else {
            System.out.println("Login failed. Check username/password.");
        }

        return fan;
    }

    public void displayProfile(Fan fan) {
        if (fan == null) {
            System.out.println("No logged-in fan.");
            return;
        }

        System.out.println("\n=== MY PROFILE ===");
        System.out.println("Fan ID : " + fan.getFanId());
        System.out.println("Username: " + fan.getUsername());
        System.out.println("Email   : " + fan.getEmail());
    }
}
