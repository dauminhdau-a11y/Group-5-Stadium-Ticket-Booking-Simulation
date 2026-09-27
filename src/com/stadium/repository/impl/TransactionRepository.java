package com.stadium.repository.impl;

import com.stadium.model.BookingTransaction;
import java.util.List;

public class TransactionRepository extends AbstractCsvRepository<BookingTransaction> {
    public List<BookingTransaction> findByFanId(String fanId) {
        return findByCondition(transaction -> fanId != null && fanId.equals(transaction.getFanId()));
    }
}
