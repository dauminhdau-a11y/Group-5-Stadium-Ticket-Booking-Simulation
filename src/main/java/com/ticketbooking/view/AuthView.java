package com.ticketbooking.view;

import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.FanController;
import com.ticketbooking.exception.AccountLockedException;
import com.ticketbooking.exception.InvalidCredentialsException;
import com.ticketbooking.model.Fan;
import com.ticketbooking.repository.FanRepository;
import java.io.IOException;
import java.util.Scanner;

public class AuthView {
    private final Scanner scanner;
    private final FanController fanController;
    private final FanView fanView;

    public AuthView(Scanner scanner){
        this(scanner, new FanController(new FanRepository("data/fans.csv")));
    }

    public AuthView(Scanner scanner, FanController fanController){
        if (scanner == null || fanController == null){
            throw new IllegalArgumentException("Scanner and fan controller must not be null.");
        }
        this.scanner = scanner;
        this.fanController = fanController;
        this.fanView = new FanView(scanner);
    }

    public void run(AuthController authController){
        if (authController == null){
            throw new IllegalArgumentException("Auth controller must not be null.");
        }
        boolean running = true;
        while (running){
            if (authController.getCurrentFan().isPresent()){
                running = showFanMenu(authController);
            } else {
                running = showGuestMenu(authController);
            }
        }
        authController.logout();
        System.out.println("Goodbye.");
    }

    private boolean showGuestMenu(AuthController authController){
        System.out.println("\n=== Stadium Ticket Booking ===");
        System.out.println("1. Fan login");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String input = readLine();
        if (input == null){
            return false;
        }
        String choice = input.trim();

        if ("1".equals(choice)){
            return login(authController);
        }
        if ("0".equals(choice)){
            return false;
        }
        System.out.println("Invalid choice.");
        return true;
    }

    private boolean showFanMenu(AuthController authController){
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
            fanView.run(fan, fanController);
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

    private boolean login(AuthController authController){
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
            Fan fan = authController.login(username, password);
            System.out.println("Login successful. Welcome, " + fan.getUsername() + "!");
        } catch (InvalidCredentialsException | AccountLockedException e){
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println("Unable to read fan accounts: " + e.getMessage());
        }
        return true;
    }

    private String readLine(){
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}