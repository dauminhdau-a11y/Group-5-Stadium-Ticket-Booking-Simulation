package com.ticketbooking.repository;

import com.ticketbooking.model.Transaction;

public class TransactionRepository extends CsvRepository<Transaction> {
    public TransactionRepository() {
        this("data/transactions.csv");
    }

    public TransactionRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Transaction parseLine(String line) {
        return new Transaction().fromCsvLine(line);
    }
}