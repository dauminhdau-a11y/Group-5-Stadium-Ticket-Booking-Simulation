package com.ticketbooking.repository;

import com.ticketbooking.model.Account;
import java.io.IOException;
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
    protected boolean isHeaderLine(String line) {
        return "id,accountId,username,passwordHash,email,role,locked"
                .equalsIgnoreCase(line.trim());
    }

    public Optional<Account> findByUsername(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        for (Account account : findAll()) {
            if (account.getUsername() != null
                    && account.getUsername().equalsIgnoreCase(username.trim())) {
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }
}
