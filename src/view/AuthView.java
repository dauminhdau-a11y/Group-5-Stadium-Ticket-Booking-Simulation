package view;

import controller.AccountController;
import controller.AdminController;
import controller.AuthController;
import controller.MatchController;
import controller.NotificationController;
import controller.StaffController;
import controller.TransactionController;
import controller.VoucherController;
import exception.AccountLockedException;
import exception.InvalidCredentialsException;
import model.Account;
import model.Admin;
import model.Fan;
import model.Match;
import model.Staff;
import model.enums.Role;
import repository.NotificationRepository;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AuthView {
    private Scanner scanner;
    private AuthController authController;
    private AccountController accountController;
    private MatchController matchController;
    private VoucherController voucherController;
    private TransactionController transactionController;

    private FanView fanView;
    private StaffView staffView;
    private AdminView adminView;
    private MatchView matchView;
    private TransactionView transactionView;

    public AuthView(Scanner scanner,
            AuthController authController,
            AccountController accountController,
            MatchController matchController,
            VoucherController voucherController,
            TransactionController transactionController,
            StaffController staffController,
            AdminController adminController) {
        this(scanner, authController, accountController, matchController, voucherController,
                transactionController, staffController, adminController,
                new NotificationController(new NotificationRepository()));
    }

    public AuthView(Scanner scanner,
            AuthController authController,
            AccountController accountController,
            MatchController matchController,
            VoucherController voucherController,
            TransactionController transactionController,
            StaffController staffController,
            AdminController adminController,
            NotificationController notificationController) {
        this.scanner = scanner;
        this.authController = authController;
        this.accountController = accountController;
        this.matchController = matchController;
        this.voucherController = voucherController;
        this.transactionController = transactionController;

        this.fanView = new FanView(scanner, matchController,
                new NotificationView(notificationController, scanner));
        this.staffView = new StaffView(staffController, scanner);
        this.adminView = new AdminView(adminController, scanner);
        this.matchView = new MatchView(matchController, scanner);
        this.transactionView = new TransactionView(transactionController, scanner);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            running = showGuestMenu();
        }
        authController.logout();
        System.out.println("Thank you for using Stadium Ticket Booking System. Goodbye!");
    }

    public boolean showGuestMenu() {
        displayBanner();
        System.out.println("\n=== GUEST MENU ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Search match");
        System.out.println("4. Filter match");
        System.out.println("0. Exit");
        System.out.print("Choose (0-4): ");

        String input = readLine();
        if (input == null) {
            return false;
        }
        String choice = input.trim();

        try {
            switch (choice) {
                case "1":
                    registerFan();
                    return true;
                case "2":
                    loginUser();
                    return true;
                case "3":
                    searchMatch();
                    return true;
                case "4":
                    filterMatch();
                    return true;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice. Please select from 0 to 4.");
                    return true;
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
            return true;
        }
    }

    private void displayBanner() {
       System.out.println("================================================================================");
        System.out.println("  _____ _             _ _                 _____ _      _        _               ");
        System.out.println(" / ____| |           | (_)               |_   _(_)    | |      | |              ");
        System.out.println("| (___ | |_ __ _   __| |_ _   _ _ __ ___   | |  _  ___| | _____| |_             ");
        System.out.println(" \\___ \\| __/ _` | / _` | | | | | '_ ` _ \\  | | | |/ __| |/ / _ \\ __|            ");
        System.out.println(" ____) | || (_| || (_| | | |_| | | | | | | | | | | (__|   <  __/ |_             ");
        System.out.println("|_____/ \\__\\__,_| \\__,_|_|\\__,_|_| |_| |_| |_| |_|\\___|_|\\_\\___|\\__|            ");
        System.out.println("                                                                                ");
        System.out.println("           STADIUM TICKET BOOKING & CONCURRENCY SIMULATION SYSTEM               ");
        System.out.println("              Group 5 - Enterprise MVC Architecture (LAB211)                    ");
        System.out.println("================================================================================");
        String text = "STADIUM TICKET";
    }

    public boolean registerFan() {
        System.out.println("\n--- GUEST REGISTRATION (FAN) ---");
        System.out.print("Username: ");
        String username = readLine();
        if (username == null || username.trim().isEmpty()) {
            return true;
        }
        System.out.print("Password (at least 6 characters): ");
        String password = readLine();
        if (password == null || password.trim().isEmpty()) {
            return true;
        }
        System.out.print("Confirm password: ");
        String confirmation = readLine();
        if (confirmation == null || !password.equals(confirmation)) {
            System.out.println("Passwords do not match.");
            return true;
        }
        System.out.print("Email: ");
        String email = readLine();
        if (email == null || email.trim().isEmpty()) {
            return true;
        }

        try {
            Fan fan = new Fan();
            fan.setUsername(username.trim());
            fan.setPasswordHash(password);
            fan.setEmail(email.trim());
            if (!authController.register(fan)) {
                System.out.println("Unable to register account. Username taken or invalid format.");
                return true;
            }
            System.out.println("Registration successful! You can now log in.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unable to save fan account: " + e.getMessage());
        }
        return true;
    }

    public boolean loginUser() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = readLine();
        if (username == null) {
            return true;
        }
        System.out.print("Password: ");
        String password = readLine();
        if (password == null) {
            return true;
        }

        try {
            Account account = authController.login(username.trim(), password);
            System.out.println("\nLogin successful! Welcome, " + account.getUsername() + " (" + account.getRole() + ")");

            if (account.getRole() == Role.FAN) {
                Fan fan = (Fan) account;
                fanView.run(fan, accountController, transactionController, voucherController, transactionView);
            } else if (account.getRole() == Role.STAFF) {
                Staff staff;
                if (account instanceof Staff) {
                    staff = (Staff) account;
                } else {
                    staff = new Staff(account.getId(), account.getAccountId(),
                            account.getUsername(), account.getPasswordHash(),
                            account.getEmail(), account.isLocked());
                }
                staffView.run(staff);
            } else if (account.getRole() == Role.ADMIN) {
                Admin admin;
                if (account instanceof Admin) {
                    admin = (Admin) account;
                } else {
                    admin = new Admin(account.getId(), account.getAccountId(),
                            account.getUsername(), account.getPasswordHash(),
                            account.getEmail(), account.isLocked());
                }
                adminView.run(admin);
            }
        } catch (InvalidCredentialsException | AccountLockedException e) {
            System.out.println("[LOGIN ERROR]: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unable to read account repository: " + e.getMessage());
        }
        return true;
    }

    public void searchMatch() throws IOException {
        System.out.print("\nEnter keyword to search match (team, stadium, ID): ");
        String kw = readLine();
        List<Match> matches = matchController.searchMatches(kw);
        matchView.displayMatches(matches);
    }

    public void filterMatch() throws IOException {
        matchView.filterMatches();
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}