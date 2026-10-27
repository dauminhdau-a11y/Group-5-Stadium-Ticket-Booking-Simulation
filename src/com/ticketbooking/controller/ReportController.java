package com.ticketbooking.controller;

import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.enums.TransactionStatus;
import com.ticketbooking.repository.TransactionRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    private TransactionRepository transactionRepository;

    public ReportController() {
        this(new TransactionRepository("data/transactions.csv"));
    }

    public ReportController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public double getTotalRevenue() throws IOException {
        double revenue = 0.0;
        for (Transaction txn : transactionRepository.findAll()) {
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                revenue += txn.getTotalAmount();
            }
        }
        return revenue;
    }

    public Map<String, Double> getRevenueByMatch() throws IOException {
        Map<String, Double> revenueByMatch = new HashMap<>();
        for (Transaction txn : transactionRepository.findAll()) {
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                String matchId = txn.getMatchId() != null ? txn.getMatchId() : "Unknown";
                double current = revenueByMatch.getOrDefault(matchId, 0.0);
                revenueByMatch.put(matchId, current + txn.getTotalAmount());
            }
        }
        return revenueByMatch;
    }

    public String generateSummaryReport() throws IOException {
        int totalTxn = 0;
        int successfulTxn = 0;
        int refundedTxn = 0;
        double totalRevenue = 0.0;
        for (Transaction txn : transactionRepository.findAll()) {
            totalTxn++;
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                successfulTxn++;
                totalRevenue += txn.getTotalAmount();
            } else if (txn.getStatus() == TransactionStatus.REFUNDED) {
                refundedTxn++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== SYSTEM REVENUE & TRANSACTION REPORT ===\n");
        sb.append("Total Transactions: ").append(totalTxn).append("\n");
        sb.append("Successful Transactions: ").append(successfulTxn).append("\n");
        sb.append("Refunded Transactions: ").append(refundedTxn).append("\n");
        sb.append("Total Revenue: ").append(String.format("%,.0f VND", totalRevenue)).append("\n");
        return sb.toString();
    }
}
