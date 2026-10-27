package com.repository.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.model.BookingTransaction;

public class BookingTransactionRepository extends AbstractCsvRepository<BookingTransaction> {
    public BookingTransactionRepository() {
        super(BookingTransaction.class, "transactions.csv");
    }

    public BookingTransactionRepository(Path dataDirectory) {
        super(BookingTransaction.class, dataDirectory.resolve("transactions.csv"));
    }

    public List<BookingTransaction> findByMatch(String matchId) {
        List<BookingTransaction> result = new ArrayList<BookingTransaction>();
        for (BookingTransaction tx : findAll()) {
            if (tx.getMatchId().equals(matchId)) {
                result.add(tx);
            }
        }
        return result;
    }
}
