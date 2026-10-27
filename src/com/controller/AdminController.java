package com.controller;

import java.util.List;
import java.util.UUID;

import com.exception.InsufficientPermissionException;
import com.model.User;
import com.model.enums.UserRole;
import com.repository.impl.StaffAccountRepository;
import com.util.PasswordHasher;

public class AdminController {
    private final StaffAccountRepository accountRepository;
    private final SessionContext session;

    public AdminController() {
        this(new StaffAccountRepository());
    }

    public AdminController(StaffAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.session = new SessionContext();
    }

    public boolean hasAdminAccount() {
        return accountRepository.hasRole(UserRole.ADMIN);
    }

    public boolean createInitialAdmin(String username, String password) {
        if (!isValidUsername(username) || !isValidPassword(password, 10)) {
            return false;
        }
        User admin = new User("ADMIN-" + UUID.randomUUID().toString(), username.trim(),
                PasswordHasher.hash(password), "", UserRole.ADMIN.name());
        return accountRepository.saveInitialAdmin(admin);
    }

    public boolean login(String username, String password) {
        User admin = accountRepository.findByUsername(username);
        if (admin == null || !UserRole.ADMIN.name().equalsIgnoreCase(admin.getRole())
                || !PasswordHasher.verify(password, admin.getPassword())) {
            return false;
        }
        session.setUser(admin);
        return true;
    }

    public User createStaffAccount(String username, String password, String email) {
        session.require(UserRole.ADMIN);
        if (!isValidUsername(username) || !isValidPassword(password, 8)) {
            throw new IllegalArgumentException("Username must be 3-32 letters, digits, dots, underscores, or hyphens; password must be at least 8 characters.");
        }
        if (accountRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("That username is already assigned.");
        }
        User staff = new User("STAFF-" + UUID.randomUUID().toString(), username.trim(),
                PasswordHasher.hash(password), email == null ? "" : email.trim(), UserRole.STAFF.name());
        if (!accountRepository.save(staff)) {
            throw new IllegalStateException("Unable to save the staff account.");
        }
        return staff;
    }

    public List<User> getStaffAccounts() {
        session.require(UserRole.ADMIN);
        return accountRepository.findByRole(UserRole.STAFF);
    }

    public boolean isLoggedIn() {
        return session.getUser() != null;
    }

    public void logout() {
        session.setUser(null);
    }

    public void showDashboard() {
        session.require(UserRole.ADMIN);
        System.out.println("Admin dashboard");
    }

    private static boolean isValidUsername(String username) {
        return username != null && username.trim().matches("[A-Za-z0-9._-]{3,32}");
    }

    private static boolean isValidPassword(String password, int minimumLength) {
        return password != null && password.length() >= minimumLength;
    }
}
