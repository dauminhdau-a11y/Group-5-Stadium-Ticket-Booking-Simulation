package com;

import java.util.Scanner;

import com.controller.AdminController;
import com.controller.StaffController;
import com.repository.impl.StaffAccountRepository;
import com.view.AdminView;
import com.view.StaffView;

public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("   STADIUM TICKET BOOKING");
        System.out.println("===============================================");

        Scanner scanner = new Scanner(System.in);
        StaffAccountRepository accountRepository = new StaffAccountRepository();
        AdminController adminController = new AdminController(accountRepository);
        AdminView adminView = new AdminView(adminController, scanner);

        if (!adminController.hasAdminAccount() && !adminView.setupInitialAdmin()) {
            return;
        }

        StaffController staffController = new StaffController(accountRepository);
        StaffView staffView = new StaffView(staffController, scanner);
        while (true) {
            System.out.println("\n=== LOGIN MENU ===");
            System.out.println("1. Staff login");
            System.out.println("2. Admin login");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "0";
            if ("1".equals(choice)) {
                staffView.showLoginScreen();
            } else if ("2".equals(choice)) {
                adminView.showLoginScreen();
            } else if ("0".equals(choice)) {
                System.out.println("Bye.");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
