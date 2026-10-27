package com.repository.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.model.Payment;

public class PaymentRepository extends AbstractCsvRepository<Payment> {
    public PaymentRepository() {
        super(Payment.class, "payments.csv");
    }

    public PaymentRepository(Path dataDirectory) {
        super(Payment.class, dataDirectory.resolve("payments.csv"));
    }

    public Payment findByTransaction(String transactionId) {
        for (Payment payment : findAll()) {
            if (transactionId != null && transactionId.equals(payment.getTransactionId())) {
                return payment;
            }
        }
        return null;
    }
}