package com.ticketbooking.controller;

import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Staff;
import com.ticketbooking.model.enums.Role;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import com.ticketbooking.view.AccountView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountController {
    private AccountRepository accountRepo;
    private AccountRepository adminRepo;
    private FanRepository fanRepo;
    private AccountView accountView;
    private AuthController authController;

    public AccountController(AccountRepository accountRepo, AccountView accountView) {
        this(accountRepo, new AccountRepository("data/admins.csv"),
                new FanRepository("data/fans.csv"), accountView);
    }

    public AccountController(AccountRepository accountRepo, AccountRepository adminRepo,
            FanRepository fanRepo, AccountView accountView) {
        this(accountRepo, adminRepo, fanRepo, accountView,
                new AuthController(fanRepo, accountRepo, adminRepo));
    }

    public AccountController(AccountRepository accountRepo, AccountRepository adminRepo,
            FanRepository fanRepo, AccountView accountView, AuthController authController) {
        this.accountRepo = accountRepo;
        this.adminRepo = adminRepo;
        this.fanRepo = fanRepo;
        this.accountView = accountView;
        this.authController = authController;
    }

    public void createStaffAccount(Staff staff) throws IOException {
        if (staff == null) {
            throw new IllegalArgumentException("Staff account is required.");
        }
        staff.setRole(Role.STAFF);
        if (!authController.register(staff)) {
            throw new IllegalArgumentException("Staff account is invalid or username is taken.");
        }
    }

    public void createFanAccount(Fan fan) throws IOException {
        if (fan == null) {
            throw new IllegalArgumentException("Fan account is required.");
        }
        fan.setRole(Role.FAN);
        if (!authController.register(fan)) {
            throw new IllegalArgumentException("Fan account is invalid or username is taken.");
        }
    }

    public boolean removeAccount(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String accountId = id.trim();
        if (fanRepo.deleteById(accountId)) {
            return true;
        }
        if (accountRepo.deleteById(accountId)) {
            return true;
        }
        return adminRepo.deleteById(accountId);
    }

    public void updateFanAccount(Fan fan) throws IOException {
        if (fan == null || fan.getId() == null || fan.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("A valid fan account is required.");
        }
        Account found = viewProfile(fan.getId());
        if (!(found instanceof Fan)) {
            throw new IllegalArgumentException("Fan account was not found.");
        }
        Fan savedFan = (Fan) found;
        String email = fan.getEmail() == null ? "" : fan.getEmail().trim();
        String phone = fan.getPhone() == null ? "" : fan.getPhone().trim();
        if (email.isEmpty() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!phone.isEmpty() && !phone.matches("^\\+?[0-9() -]{8,20}$")) {
            throw new IllegalArgumentException("Enter a valid phone number.");
        }
        if (fan.getDateOfBirth() != null
                && fan.getDateOfBirth().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }

        savedFan.setEmail(email);
        savedFan.setPhone(phone.isEmpty() ? null : phone);
        savedFan.setDateOfBirth(fan.getDateOfBirth());
        fanRepo.save(savedFan);

        fan.setEmail(savedFan.getEmail());
        fan.setPhone(savedFan.getPhone());
        fan.setDateOfBirth(savedFan.getDateOfBirth());
    }

    public List<Fan> searchFanAccount(String keyword) throws IOException {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Fan> results = new ArrayList<>();
        for (Fan fan : fanRepo.findAll()) {
            if (query.isEmpty()
                    || contains(fan.getUsername(), query)
                    || contains(fan.getEmail(), query)
                    || contains(fan.getId(), query)) {
                results.add(fan);
            }
        }
        return results;
    }

    public void lockFanAccount(String id) throws IOException {
        Account found = viewProfile(id);
        if (!(found instanceof Fan)) {
            throw new IllegalArgumentException("Fan account was not found.");
        }
        Fan fan = (Fan) found;
        fan.setLocked(true);
        fanRepo.save(fan);
    }

    public Account viewProfile(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID is required.");
        }
        String accountId = id.trim();
        for (Fan fan : fanRepo.findAll()) {
            if (accountId.equals(fan.getId())) {
                return fan;
            }
        }
        for (Account account : accountRepo.findAll()) {
            if (accountId.equals(account.getId())) {
                return account;
            }
        }
        for (Account account : adminRepo.findAll()) {
            if (accountId.equals(account.getId())) {
                return account;
            }
        }
        throw new IllegalArgumentException("Account was not found.");
    }

    public List<Account> findByRole(Role role) throws IOException {
        List<Account> accounts = new ArrayList<>();
        if (role == null) {
            return accounts;
        }
        if (role == Role.FAN) {
            accounts.addAll(fanRepo.findAll());
        } else if (role == Role.STAFF) {
            accounts.addAll(accountRepo.findByRole(role));
        } else if (role == Role.ADMIN) {
            accounts.addAll(adminRepo.findByRole(role));
        }
        return accounts;
    }

    public AccountView getAccountView() {
        return accountView;
    }

    public void setAccountView(AccountView accountView) {
        this.accountView = accountView;
    }

    public boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}