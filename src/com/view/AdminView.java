package com.view;

import java.util.List;
import java.util.Scanner;

import com.controller.AdminController;
import com.model.User;

public class AdminView {
    private final AdminController adminController;
    private final Scanner scanner;

    public AdminView(AdminController adminController, Scanner scanner) {
        this.adminController = adminController;
        this.scanner = scanner;
    }

    public boolean setupInitialAdmin() {
        System.out.println("\n=== INITIAL ADMIN SETUP ===");
        System.out.println("Create the first administrator account.");
        System.out.print("Admin username: ");
        String username = readLine();
        System.out.print("Admin password (at least 10 characters): ");
        String password = readLine();
        System.out.print("Confirm password: ");
        String confirmation = readLine();
        if (!password.equals(confirmation)) {
            System.out.println("Passwords do not match.");
            return false;
        }
        if (!adminController.createInitialAdmin(username, password)) {
            System.out.println("Could not create admin. Check username/password rules and account storage.");
            return false;
        }
        System.out.println("Admin account created.");
        return true;
    }

    public void showLoginScreen() {
        System.out.println("\n=== ADMIN LOGIN ===");
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();
        if (!adminController.login(username, password)) {
            System.out.println("Invalid username or password.");
            return;
        }
        showMenu();
    }

    public void showMenu() {
        while (adminController.isLoggedIn()) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Create staff account");
            System.out.println("2. List staff accounts");
            System.out.println("3. Logout");
            System.out.print("Choose: ");
            int choice = readInt();
            switch (choice) {
                case 1:
                    createStaffAccount();
                    break;
                case 2:
                    listStaffAccounts();
                    break;
                case 3:
                    adminController.logout();
                    System.out.println("Admin logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void render() {
        adminController.showDashboard();
    }

    private void createStaffAccount() {
        System.out.print("Staff username: ");
        String username = readLine();
        System.out.print("Staff password (at least 8 characters): ");
        String password = readLine();
        System.out.print("Email (optional): ");
        String email = readLine();
        try {
            User staff = adminController.createStaffAccount(username, password, email);
            System.out.println("Staff account created: " + staff.getUsername());
        } catch (Exception exception) {
            System.out.println("[ERROR] " + exception.getMessage());
        }
    }

    private void listStaffAccounts() {
        List<User> staffAccounts = adminController.getStaffAccounts();
        if (staffAccounts.isEmpty()) {
            System.out.println("No staff accounts have been provisioned.");
            return;
        }
        System.out.printf("%-38s %-24s %s%n", "Staff ID", "Username", "Email");
        for (User staff : staffAccounts) {
            System.out.printf("%-38s %-24s %s%n", staff.getUserId(), staff.getUsername(), staff.getEmail());
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }
}
