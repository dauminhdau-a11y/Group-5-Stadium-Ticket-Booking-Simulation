package com.ticketbooking.controller;

import com.ticketbooking.exception.AccountLockedException;
import com.ticketbooking.exception.InvalidCredentialsException;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.enums.Role;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class AuthController {
    private FanRepository fanRepository;
    private AccountRepository staffRepository;
    private AccountRepository adminRepository;
    private Account currentAccount;

    public AuthController(FanRepository fanRepository) {
        this(fanRepository, new AccountRepository("data/staff.csv"),
                new AccountRepository("data/admins.csv"));
    }

    public AuthController(FanRepository fanRepository, AccountRepository staffRepository,
            AccountRepository adminRepository) {
        if (fanRepository == null || staffRepository == null || adminRepository == null) {
            throw new IllegalArgumentException("Account repositories must not be null.");
        }
        this.fanRepository = fanRepository;
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
    }

    public Account login(String username, String password) throws IOException, InvalidCredentialsException, AccountLockedException {
        return login(username, password, null);
    }

    public Account login(String username, String password, Role expectedRole)
            throws IOException, InvalidCredentialsException, AccountLockedException {
        if (username == null || password == null) {
            throw new InvalidCredentialsException();
        }
        Optional<? extends Account> accountResult = expectedRole == null
                ? findByUsername(username)
                : findByUsername(username, expectedRole);
        if (!accountResult.isPresent()
                || (expectedRole != null && accountResult.get().getRole() != expectedRole)
                || !passwordMatches(password, accountResult.get().getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        Account account = accountResult.get();
        verifyUnlocked(account);
        currentAccount = account;
        return account;
    }

    private Optional<? extends Account> findByUsername(String username, Role role) throws IOException {
        switch (role) {
            case FAN:
                return fanRepository.findByUsername(username);
            case STAFF:
                return staffRepository.findByUsername(username);
            case ADMIN:
                return adminRepository.findByUsername(username);
            default:
                throw new IllegalArgumentException("Unsupported account role: " + role);
        }
    }

    public boolean register(Account account) throws IOException {
        if (account == null || account.getRole() == null || account.getUsername() == null
                || account.getUsername().trim().isEmpty()
                || account.getPasswordHash() == null || account.getPasswordHash().length() < 6
                || account.getEmail() == null
                || !account.getEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return false;
        }
        if (account.getRole() == Role.FAN && !(account instanceof Fan)) {
            return false;
        }
        if (account instanceof Fan) {
            Fan fan = (Fan) account;
            String phone = fan.getPhone();
            if (phone != null && !phone.trim().isEmpty()
                    && !phone.trim().matches("^\\+?[0-9() -]{8,20}$")) {
                return false;
            }
            if (fan.getDateOfBirth() != null
                    && fan.getDateOfBirth().isAfter(java.time.LocalDate.now())) {
                return false;
            }
        }
        if (usernameExists(account.getUsername())) {
            return false;
        }

        String id = account.getId();
        if (id == null || id.trim().isEmpty()) {
            id = account.getRole().name() + "_" + UUID.randomUUID().toString().substring(0, 8);
            account.setId(id);
        } else if (accountIdExists(id.trim())) {
            return false;
        }
        account.setAccountId(id);
        account.setUsername(account.getUsername().trim());
        account.setEmail(account.getEmail().trim());
        account.setPasswordHash(normalizePasswordHash(account.getPasswordHash()));
        account.setLocked(false);

        switch (account.getRole()) {
            case FAN:
                if (!(account instanceof Fan)) {
                    return false;
                }
                fanRepository.save((Fan) account);
                break;
            case STAFF:
                staffRepository.save(account);
                break;
            case ADMIN:
                adminRepository.save(account);
                break;
            default:
                return false;
        }
        return true;
    }

    public void logout() {
        currentAccount = null;
    }

    public Optional<Fan> getCurrentFan() {
        return currentAccount instanceof Fan
                ? Optional.of((Fan) currentAccount) : Optional.empty();
    }

    public Optional<Account> getCurrentAccount() {
        return Optional.ofNullable(currentAccount);
    }

    public Optional<? extends Account> findByUsername(String username) throws IOException {
        Optional<Fan> fan = fanRepository.findByUsername(username);
        if (fan.isPresent()) {
            return fan;
        }
        Optional<Account> staff = staffRepository.findByUsername(username);
        if (staff.isPresent()) {
            return staff;
        }
        return adminRepository.findByUsername(username);
    }

    public void verifyUnlocked(Account account) throws AccountLockedException {
        if (account.isLocked()) {
            throw new AccountLockedException();
        }
    }

    public String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte current : hash) {
                hex.append(String.format(Locale.ROOT, "%02x", current));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }

    public boolean usernameExists(String username) throws IOException {
        return fanRepository.findByUsername(username).isPresent()
                || staffRepository.findByUsername(username).isPresent()
                || adminRepository.findByUsername(username).isPresent();
    }

    public boolean accountIdExists(String id) throws IOException {
        return fanRepository.findAll().stream().anyMatch(account -> id.equals(account.getId()))
                || staffRepository.findAll().stream().anyMatch(account -> id.equals(account.getId()))
                || adminRepository.findAll().stream().anyMatch(account -> id.equals(account.getId()));
    }

    public String normalizePasswordHash(String password) {
        String normalized = password.trim();
        if (normalized.matches("(?i)(sha256:)?[0-9a-f]{64}")) {
            return normalized;
        }
        return hashPassword(password);
    }

    public boolean passwordMatches(String password, String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        String trimmedStored = storedPassword.trim();
        if (password.equals(trimmedStored)) {
            return true;
        }
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return false;
        }
        String normalizedStored = trimmedStored.toLowerCase(Locale.ROOT);
        String normalizedHashed = hashedPassword.toLowerCase(Locale.ROOT);
        if (normalizedStored.equals(normalizedHashed)) {
            return true;
        }
        if (trimmedStored.startsWith("sha256:")) {
            String storedHash = trimmedStored.substring("sha256:".length()).toLowerCase(Locale.ROOT);
            return storedHash.equals(normalizedHashed);
        }
        return false;
    }
}