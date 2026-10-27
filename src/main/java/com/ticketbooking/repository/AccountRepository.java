package com.ticketbooking.repository;

import com.ticketbooking.model.Account;
import com.ticketbooking.model.enums.Role;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository extends CsvRepository<Account> {
    public AccountRepository() {
        super("accounts.csv");
    }

    public AccountRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Account parseLine(String line) {
        return new Account().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "id,accountId,username,passwordHash,email,role,locked"
                .equalsIgnoreCase(line.trim());
    }

    public Optional<Account> findByUsername(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalizedUsername = username.trim();
        return findFirst(account -> account.getUsername() != null
                && account.getUsername().equalsIgnoreCase(normalizedUsername));
    }

    public List<Account> findByRole(Role role) throws IOException {
        List<Account> accounts = new ArrayList<>();
        if (role == null) {
            return accounts;
        }
        for (Account account : findAll()) {
            if (account.getRole() == role) {
                accounts.add(account);
            }
        }
        return accounts;
    }
}
