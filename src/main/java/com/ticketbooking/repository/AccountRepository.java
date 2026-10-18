package com.ticketbooking.repository;

import com.ticketbooking.model.Account;

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
}
