package com.ticketbooking.view;

import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.AccountController;
import com.ticketbooking.exception.AccountLockedException;
import com.ticketbooking.exception.InvalidCredentialsException;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.enums.Role;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import java.io.IOException;
import java.util.Scanner;

public class AuthView {
    private Scanner scanner;
    private FanView fanView;
    private AccountController accountController;
    private AccountView accountView;

    public AuthView(Scanner scanner){
        this(scanner, createDefaultAccountController(scanner));
    }

    public AuthView(Scanner scanner, AccountController accountController){
        if (scanner == null || accountController == null){
            throw new IllegalArgumentException("Scanner and controllers must not be null.");
        }
        this.scanner = scanner;
        this.fanView = new FanView(scanner);
        this.accountController = accountController;
        this.accountView = accountController.getAccountView();
    }

    private static AccountController createDefaultAccountController(Scanner scanner) {
        FanRepository fanRepository = new FanRepository("data/fans.csv");
        AccountRepository staffRepository = new AccountRepository("data/staff.csv");
        AccountRepository adminRepository = new AccountRepository("data/admins.csv");
        AuthController authController =
                new AuthController(fanRepository, staffRepository, adminRepository);
        return new AccountController(staffRepository, adminRepository, fanRepository,
                new AccountView(scanner), authController);
    }

    public void run(AuthController authController){
        if (authController == null){
            throw new IllegalArgumentException("Auth controller must not be null.");
        }
        boolean running = true;
        while (running){
            if (authController.getCurrentAccount().isPresent()){
                Account account = authController.getCurrentAccount().get();
                if (account.getRole() == Role.FAN) {
                    running = showFanMenu(authController);
                } else {
                    running = showRoleMenu(authController, account);
                }
            } else {
                running = showGuestMenu(authController);
            }
        }
        authController.logout();
        System.out.println("Goodbye.");
    }

    public boolean showGuestMenu(AuthController authController){
        System.out.println("\n=== Stadium Ticket Booking ===");
        System.out.println("1. Fan login");
        System.out.println("2. Staff login");
        System.out.println("3. Admin login");
        System.out.println("4. Register fan");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String input = readLine();
        if (input == null){
            return false;
        }
        String choice = input.trim();

        if ("1".equals(choice)){
            return login(authController, Role.FAN);
        }
        if ("2".equals(choice)){
            return login(authController, Role.STAFF);
        }
        if ("3".equals(choice)){
            return login(authController, Role.ADMIN);
        }
        if ("4".equals(choice)){
            return registerFan(authController);
        }
        if ("0".equals(choice)){
            return false;
        }
        System.out.println("Invalid choice.");
        return true;
    }

    public boolean showRoleMenu(AuthController authController, Account account) {
        Role role = account.getRole();
        System.out.println("\n=== " + (role == Role.STAFF ? "Staff" : "Admin") + " menu ===");
        System.out.println("Welcome, " + account.getUsername() + "!");
        System.out.println("Role: " + role);
        if (role == Role.ADMIN) {
            System.out.println("1. Manage accounts");
            System.out.println("2. Logout");
        } else {
            System.out.println("1. Logout");
        }
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String input = readLine();
        if (input == null) {
            return false;
        }
        String choice = input.trim();

        if (role == Role.ADMIN && "1".equals(choice)) {
            accountView.run(accountController);
            return true;
        }
        if ((role == Role.ADMIN && "2".equals(choice))
                || (role != Role.ADMIN && "1".equals(choice))) {
            authController.logout();
            System.out.println("You have logged out.");
            return true;
        }
        if ("0".equals(choice)) {
            return false;
        }
        System.out.println("Invalid choice.");
        return true;
    }

    public boolean showFanMenu(AuthController authController){
        Fan fan = authController.getCurrentFan().get();
        System.out.println("\n=== Fan menu ===");
        System.out.println("Welcome, " + fan.getUsername() + "!");
        System.out.println("1. Manage profile");
        System.out.println("2. Logout");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String input = readLine();
        if (input == null){
            return false;
        }
        String choice = input.trim();

        if ("1".equals(choice)){
            fanView.run(fan, accountController);
            return true;
        }
        if ("2".equals(choice)){
            authController.logout();
            System.out.println("You have logged out.");
            return true;
        }
        if ("0".equals(choice)){
            return false;
        }
        System.out.println("Invalid choice.");
        return true;
    }

    public boolean login(AuthController authController, Role role){
        System.out.print("Username: ");
        String username = readLine();
        if (username == null){
            return false;
        }
        System.out.print("Password: ");
        String password = readLine();
        if (password == null){
            return false;
        }

        try {
            Account account = authController.login(username, password);
            if (account.getRole() != role) {
                authController.logout();
                throw new InvalidCredentialsException();
            }
            System.out.println("Login successful. Welcome, " + account.getUsername() + "!");
        } catch (InvalidCredentialsException | AccountLockedException e){
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println("Unable to read " + role.name().toLowerCase()
                    + " accounts: " + e.getMessage());
        }
        return true;
    }

    public String readLine(){
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
    
    public boolean registerFan(AuthController authController){
        System.out.print("Username: ");
        String username = readLine();
        if (username == null){
            return false;
        }
        System.out.print("Password: ");
        String password = readLine();
        if (password == null){
            return false;
        }
        System.out.print("Email: ");
        String email = readLine();
        if (email == null){
            return false;
        }
        System.out.print("Confirm password:");
        String confirmation = readLine();
        if (confirmation == null){
            return false;
        }
        if (!password.equals(confirmation)){
            System.out.println("Passwords do not match.");
            return true;
        }
        try{
            Fan fan = new Fan();
            fan.setUsername(username.trim());
            fan.setPasswordHash(password);
            fan.setEmail(email.trim());
            if (!authController.register(fan)) {
                System.out.println("Unable to register account. Check the details or username.");
                return true;
            }
            System.out.println("Registration successful. You can now log in.");
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            return true;
        } catch (IOException e){
            System.out.println("Unable to save fan account: " + e.getMessage());
            return true;
        }
        return true;
    }
}