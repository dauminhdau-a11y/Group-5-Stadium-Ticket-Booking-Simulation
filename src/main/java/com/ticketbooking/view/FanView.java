package com.ticketbooking.view;

import com.ticketbooking.controller.AccountController;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class FanView {
    private Scanner scanner;

    public FanView(Scanner scanner){
        if (scanner == null){
            throw new IllegalArgumentException("Scanner must not be null.");
        }
        this.scanner = scanner;
    }

    public void run(Fan fan, AccountController accountController){
        if (fan == null || accountController == null){
            throw new IllegalArgumentException("Fan and account controller must not be null.");
        }

        boolean running = true;
        while (running){
            System.out.println("\n=== Fan profile ===");
            System.out.println("1. View profile");
            System.out.println("2. Edit profile");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = readLine();
            if (choice == null){
                return;
            }

            switch (choice.trim()){
                case "1":
                    showProfile(fan, accountController);
                    break;
                case "2":
                    editProfile(fan, accountController);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void showProfile(Fan fan, AccountController accountController){
        try {
            Account account = accountController.viewProfile(fan.getId());
            if (!(account instanceof Fan)) {
                throw new IllegalArgumentException("Fan account was not found.");
            }
            Fan profile = (Fan) account;
            System.out.println("\n--- Profile ---");
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Email: " + valueOrDefault(profile.getEmail()));
            System.out.println("Phone: " + valueOrDefault(profile.getPhone()));
            System.out.println("Date of birth: " + valueOrDefault(
                    profile.getDateOfBirth() == null ? null : profile.getDateOfBirth().toString()));
            System.out.println("Loyalty points: " + profile.getLoyaltyPoints());
        } catch (IOException | IllegalArgumentException e){
            System.out.println("Unable to load profile: " + e.getMessage());
        }
    }

    public void editProfile(Fan fan, AccountController accountController){
        try {
            Account account = accountController.viewProfile(fan.getId());
            if (!(account instanceof Fan)) {
                throw new IllegalArgumentException("Fan account was not found.");
            }
            Fan profile = (Fan) account;
            System.out.println("Leave a field blank to keep its current value.");
            System.out.print("Email [" + valueOrDefault(profile.getEmail()) + "]: ");
            String emailInput = readLine();
            if (emailInput == null){
                return;
            }
            System.out.print("Phone [" + valueOrDefault(profile.getPhone()) + "]: ");
            String phoneInput = readLine();
            if (phoneInput == null){
                return;
            }
            System.out.print("Date of birth (YYYY-MM-DD) ["
                    + valueOrDefault(profile.getDateOfBirth() == null
                            ? null : profile.getDateOfBirth().toString())
                    + "]: ");
            String dateInput = readLine();
            if (dateInput == null){
                return;
            }

            String email = emailInput.trim().isEmpty() ? profile.getEmail() : emailInput.trim();
            String phone = phoneInput.trim().isEmpty() ? profile.getPhone() : phoneInput.trim();
            LocalDate dateOfBirth = profile.getDateOfBirth();
            if (!dateInput.trim().isEmpty()){
                dateOfBirth = LocalDate.parse(dateInput.trim());
            }

            profile.setEmail(email);
            profile.setPhone(phone);
            profile.setDateOfBirth(dateOfBirth);
            accountController.updateFanAccount(profile);
            System.out.println("Profile updated successfully.");
        } catch (DateTimeParseException e){
            System.out.println("Invalid date. Use YYYY-MM-DD.");
        } catch (IOException | IllegalArgumentException e){
            System.out.println("Unable to update profile: " + e.getMessage());
        }
    }

    public String valueOrDefault(String value){
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    public String readLine(){
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}