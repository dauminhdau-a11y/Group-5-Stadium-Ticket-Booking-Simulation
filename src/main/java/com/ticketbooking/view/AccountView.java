package com.ticketbooking.view;

import com.ticketbooking.controller.AccountController;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Staff;
import com.ticketbooking.model.enums.Role;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AccountView {
    Scanner scanner;

    public AccountView(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner must not be null.");
        }
        this.scanner = scanner;
    }

    public Account readAccountInput() {
        System.out.println("Create account type: 1. Fan  2. Staff");
        System.out.print("Choose: ");
        String type = readLine();
        if (type == null) {
            return null;
        }

        Role role;
        if ("1".equals(type.trim())) {
            role = Role.FAN;
        } else if ("2".equals(type.trim())) {
            role = Role.STAFF;
        } else {
            System.out.println("Invalid account type.");
            return null;
        }

        System.out.print("Username: ");
        String username = readLine();
        if (username == null) {
            return null;
        }
        System.out.print("Password (at least 8 characters): ");
        String password = readLine();
        if (password == null) {
            return null;
        }
        System.out.print("Email: ");
        String email = readLine();
        if (email == null) {
            return null;
        }

        Account account;
        if (role == Role.FAN) {
            account = new Fan();
        } else {
            account = new Staff();
        }
        account.setUsername(username.trim());
        account.setPasswordHash(password);
        account.setEmail(email.trim());
        return account;
    }

    public void showAccountList(List<? extends Account> accounts) {
        System.out.println("\n--- Accounts ---");
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.printf("%-38s %-20s %-24s %-8s %-8s%n",
                "ID", "Username", "Email", "Role", "Locked");
        for (Account account : accounts) {
            System.out.printf("%-38s %-20s %-24s %-8s %-8s%n",
                    valueOrDash(account.getId()), valueOrDash(account.getUsername()),
                    valueOrDash(account.getEmail()), account.getRole(), account.isLocked());
        }
    }

    public void showProfile(Account account) {
        if (account == null) {
            System.out.println("Account was not found.");
            return;
        }
        System.out.println("\n--- Account profile ---");
        System.out.println("ID: " + valueOrDash(account.getId()));
        System.out.println("Username: " + valueOrDash(account.getUsername()));
        System.out.println("Email: " + valueOrDash(account.getEmail()));
        System.out.println("Role: " + account.getRole());
        System.out.println("Locked: " + account.isLocked());
        if (account instanceof Fan) {
            Fan fan = (Fan) account;
            System.out.println("Phone: " + valueOrDash(fan.getPhone()));
            System.out.println("Date of birth: " + valueOrDash(
                    fan.getDateOfBirth() == null ? null : fan.getDateOfBirth().toString()));
            System.out.println("Loyalty points: " + fan.getLoyaltyPoints());
        }
    }

    public int showAccountMenu() {
        System.out.println("\n=== Account management ===");
        System.out.println("1. List accounts by role");
        System.out.println("2. Search fan account");
        System.out.println("3. View account profile");
        System.out.println("4. Create fan/staff account");
        System.out.println("5. Lock fan account");
        System.out.println("6. Remove account");
        System.out.println("7. Update fan profile");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        String choice = readLine();
        if (choice == null) {
            return -1;
        }
        try {
            return Integer.parseInt(choice.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void run(AccountController controller) {
        boolean running = true;
        while (running) {
            int choice = showAccountMenu();
            if (choice < 0) {
                return;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Role (FAN/STAFF/ADMIN): ");
                        String roleInput = readLine();
                        if (roleInput != null) {
                            try {
                                showAccountList(controller.findByRole(
                                        Role.valueOf(roleInput.trim().toUpperCase(Locale.ROOT))));
                            } catch (IllegalArgumentException e) {
                                System.out.println("Unknown role. Enter FAN, STAFF, or ADMIN.");
                            }
                        }
                        break;
                    case 2:
                        System.out.print("Search keyword (username/email/ID): ");
                        String keyword = readLine();
                        if (keyword != null) {
                            showAccountList(controller.searchFanAccount(keyword));
                        }
                        break;
                    case 3:
                        System.out.print("Account ID: ");
                        String profileId = readLine();
                        if (profileId != null) {
                            showProfile(controller.viewProfile(profileId));
                        }
                        break;
                    case 4:
                        Account account = readAccountInput();
                        if (account instanceof Fan) {
                            controller.createFanAccount((Fan) account);
                            System.out.println("Fan account created.");
                        } else if (account instanceof Staff) {
                            controller.createStaffAccount((Staff) account);
                            System.out.println("Staff account created.");
                        }
                        break;
                    case 5:
                        System.out.print("Fan ID to lock: ");
                        String fanId = readLine();
                        if (fanId != null) {
                            controller.lockFanAccount(fanId);
                            System.out.println("Fan account locked.");
                        }
                        break;
                    case 6:
                        System.out.print("Account ID to remove: ");
                        String accountId = readLine();
                        if (accountId != null) {
                            System.out.println(controller.removeAccount(accountId)
                                    ? "Account removed." : "Account was not found.");
                        }
                        break;
                    case 7:
                        updateFanProfile(controller);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IOException e) {
                System.out.println("Unable to access accounts: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void updateFanProfile(AccountController controller) throws IOException {
        System.out.print("Fan ID to update: ");
        String fanId = readLine();
        if (fanId == null) {
            return;
        }
        Account account = controller.viewProfile(fanId);
        if (!(account instanceof Fan)) {
            System.out.println("The selected account is not a fan account.");
            return;
        }

        Fan fan = (Fan) account;
        System.out.print("Email [" + valueOrDash(fan.getEmail()) + "]: ");
        String emailInput = readLine();
        if (emailInput == null) {
            return;
        }
        System.out.print("Phone [" + valueOrDash(fan.getPhone()) + "]: ");
        String phoneInput = readLine();
        if (phoneInput == null) {
            return;
        }
        System.out.print("Date of birth (YYYY-MM-DD) ["
                + valueOrDash(fan.getDateOfBirth() == null
                        ? null : fan.getDateOfBirth().toString())
                + "]: ");
        String dateInput = readLine();
        if (dateInput == null) {
            return;
        }

        if (!emailInput.trim().isEmpty()) {
            fan.setEmail(emailInput.trim());
        }
        if (!phoneInput.trim().isEmpty()) {
            fan.setPhone(phoneInput.trim());
        }
        if (!dateInput.trim().isEmpty()) {
            try {
                fan.setDateOfBirth(LocalDate.parse(dateInput.trim()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date. Use YYYY-MM-DD.");
            }
        }
        controller.updateFanAccount(fan);
        System.out.println("Fan profile updated.");
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    public String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }
}