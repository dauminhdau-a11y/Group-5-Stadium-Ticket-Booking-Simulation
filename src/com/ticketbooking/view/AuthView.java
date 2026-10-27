package com.ticketbooking.view;

import com.ticketbooking.controller.AccountController;
import com.ticketbooking.controller.AdminController;
import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.MatchController;
import com.ticketbooking.controller.ReportController;
import com.ticketbooking.controller.SeatController;
import com.ticketbooking.controller.SectionController;
import com.ticketbooking.controller.SimulatorController;
import com.ticketbooking.controller.StadiumController;
import com.ticketbooking.controller.StaffController;
import com.ticketbooking.controller.TicketController;
import com.ticketbooking.controller.TransactionController;
import com.ticketbooking.controller.VoucherController;
import com.ticketbooking.exception.AccountLockedException;
import com.ticketbooking.exception.InvalidCredentialsException;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Admin;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Match;
import com.ticketbooking.model.Staff;
import com.ticketbooking.model.enums.MatchStatus;
import com.ticketbooking.model.enums.Role;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AuthView {
    private Scanner scanner;
    private AuthController authController;
    private AccountController accountController;
    private MatchController matchController;
    private StadiumController stadiumController;
    private SectionController sectionController;
    private SeatController seatController;
    private TicketController ticketController;
    private VoucherController voucherController;
    private TransactionController transactionController;
    private StaffController staffController;
    private AdminController adminController;
    private SimulatorController simulatorController;
    private ReportController reportController;

    private FanView fanView;
    private StaffView staffView;
    private AdminView adminView;
    private MatchView matchView;
    private TransactionView transactionView;
    private AccountView accountView;

    public AuthView(Scanner scanner,
            AuthController authController,
            AccountController accountController,
            MatchController matchController,
            StadiumController stadiumController,
            SectionController sectionController,
            SeatController seatController,
            TicketController ticketController,
            VoucherController voucherController,
            TransactionController transactionController,
            StaffController staffController,
            AdminController adminController,
            SimulatorController simulatorController,
            ReportController reportController) {
        this.scanner = scanner;
        this.authController = authController;
        this.accountController = accountController;
        this.matchController = matchController;
        this.stadiumController = stadiumController;
        this.sectionController = sectionController;
        this.seatController = seatController;
        this.ticketController = ticketController;
        this.voucherController = voucherController;
        this.transactionController = transactionController;
        this.staffController = staffController;
        this.adminController = adminController;
        this.simulatorController = simulatorController;
        this.reportController = reportController;

        this.fanView = new FanView(scanner);
        this.staffView = new StaffView(staffController, scanner);
        this.adminView = new AdminView(adminController, scanner);
        this.matchView = new MatchView(matchController, scanner);
        this.transactionView = new TransactionView(transactionController, scanner);
        this.accountView = accountController.getAccountView();
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
        System.out.println("\n==================================================");
        System.out.println("   STADIUM TICKET BOOKING SYSTEM - GUEST MENU     ");
        System.out.println("==================================================");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Search match");
        System.out.println("4. View match detail");
        System.out.println("5. Filter match");
        System.out.println("0. Exit");
        System.out.print("Choose (0-5): ");

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
                    viewMatchDetail();
                    return true;
                case "5":
                    filterMatch();
                    return true;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice. Please select from 0 to 5.");
                    return true;
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
            return true;
        }
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
        System.out.println("Select account role:");
        System.out.println("1. Fan");
        System.out.println("2. Staff");
        System.out.println("3. Admin");
        System.out.println("0. Back");
        System.out.print("Choose role (0-3): ");
        String roleChoice = readLine();
        if (roleChoice == null || "0".equals(roleChoice.trim())) {
            return true;
        }

        Role selectedRole;
        switch (roleChoice.trim()) {
            case "1":
                selectedRole = Role.FAN;
                break;
            case "2":
                selectedRole = Role.STAFF;
                break;
            case "3":
                selectedRole = Role.ADMIN;
                break;
            default:
                System.out.println("Invalid role. Please select Fan, Staff, or Admin.");
                return true;
        }

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
            Account account = authController.login(username.trim(), password, selectedRole);
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

    public void viewMatchDetail() throws IOException {
        System.out.print("\nEnter Match ID to view details: ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            return;
        }
        Match match = matchController.getMatchById(matchId.trim());
        matchView.displayMatchDetail(match);
    }

    public void filterMatch() throws IOException {
        System.out.println("\n--- Filter Matches ---");
        System.out.println("Choose status to filter: 1. SCHEDULED  2. UPCOMING  3. ONGOING  4. FINISHED  5. Any");
        System.out.print("Choose (1-5): ");
        String sChoice = readLine();
        MatchStatus status = null;
        if ("1".equals(sChoice)) status = MatchStatus.SCHEDULED;
        else if ("2".equals(sChoice)) status = MatchStatus.UPCOMING;
        else if ("3".equals(sChoice)) status = MatchStatus.ONGOING;
        else if ("4".equals(sChoice)) status = MatchStatus.FINISHED;

        System.out.print("From Date (YYYY-MM-DD or leave blank): ");
        String fromStr = readLine();
        LocalDate fromDate = null;
        if (fromStr != null && !fromStr.trim().isEmpty()) {
            try {
                fromDate = LocalDate.parse(fromStr.trim());
            } catch (DateTimeParseException ignored) {}
        }

        System.out.print("To Date (YYYY-MM-DD or leave blank): ");
        String toStr = readLine();
        LocalDate toDate = null;
        if (toStr != null && !toStr.trim().isEmpty()) {
            try {
                toDate = LocalDate.parse(toStr.trim());
            } catch (DateTimeParseException ignored) {}
        }

        List<Match> filtered = matchController.filterMatches(status, fromDate, toDate);
        matchView.displayMatches(filtered);
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}