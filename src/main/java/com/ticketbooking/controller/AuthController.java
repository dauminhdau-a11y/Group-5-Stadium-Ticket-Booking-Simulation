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
import java.util.Optional;

public class AuthController {
    private final FanRepository fanRepository;
    private final AccountRepository staffRepository;
    private final AccountRepository adminRepository;
    private Account currentAccount;

    public AuthController(FanRepository fanRepository){
        this(fanRepository, new AccountRepository("data/staff.csv"),
                new AccountRepository("data/admins.csv"));
    }

    public AuthController(FanRepository fanRepository, AccountRepository staffRepository,
            AccountRepository adminRepository){
        if (fanRepository == null || staffRepository == null || adminRepository == null){
            throw new IllegalArgumentException("Account repositories must not be null.");
        }
        this.fanRepository = fanRepository;
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
    }

    public Fan login(String username, String password) throws IOException{
        if (username == null || password == null){
            throw new InvalidCredentialsException();
        }
        Optional<Fan> fanResult = fanRepository.findByUsername(username);
        if (!fanResult.isPresent() || !passwordMatches(password, fanResult.get().getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        Fan fan = fanResult.get();
        verifyUnlocked(fan);
        currentAccount = fan;
        return fan;
    }

    public Account loginStaff(String username, String password) throws IOException {
        return login(username, password, staffRepository, Role.STAFF);
    }

    public Account loginAdmin(String username, String password) throws IOException {
        return login(username, password, adminRepository, Role.ADMIN);
    }

    public void logout(){
        currentAccount = null;
    }

    public Optional<Fan> getCurrentFan(){
        return currentAccount instanceof Fan
                ? Optional.of((Fan) currentAccount) : Optional.empty();
    }

    public Optional<Account> getCurrentAccount() {
        return Optional.ofNullable(currentAccount);
    }

    private Account login(String username, String password, AccountRepository repository,
            Role requiredRole) throws IOException {
        if (username == null || password == null) {
            throw new InvalidCredentialsException();
        }
        Optional<Account> accountResult = repository.findByUsername(username);
        if (!accountResult.isPresent()
                || accountResult.get().getRole() != requiredRole
                || !passwordMatches(password, accountResult.get().getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        Account account = accountResult.get();
        verifyUnlocked(account);
        currentAccount = account;
        return account;
    }

    private void verifyUnlocked(Account account) {
        if (account.isLocked()) {
            throw new AccountLockedException();
        }
    }

    private boolean passwordMatches(String password, String storedPassword){
        if (storedPassword == null){
            return false;
        }
        return MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }
}