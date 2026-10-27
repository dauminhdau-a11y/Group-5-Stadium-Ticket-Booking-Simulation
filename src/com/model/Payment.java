package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.util.CsvParser;

public class Payment extends SimpleEntity {
    private String paymentId;
    private String transactionId;
    private double amount;
    private String method;
    private String status;
    private double amountReceived;
    private double changeAmount;
    private String reference;
    private LocalDateTime createdAt;

    public Payment() { }

    public Payment(String paymentId, double amount, String method, String status) {
        this(paymentId, "", amount, method, status, amount, 0, "", LocalDateTime.now());
    }

    public Payment(String paymentId, String transactionId, double amount, String method, String status,
                   double amountReceived, double changeAmount, String reference, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.amountReceived = amountReceived;
        this.changeAmount = changeAmount;
        this.reference = reference;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    @Override public String getId() { return paymentId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getAmountReceived() { return amountReceived; }
    public void setAmountReceived(double amountReceived) { this.amountReceived = amountReceived; }
    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean processPayment() { return false; }
    public boolean processRefund() { return false; }

    @Override public String toCsvLine() {
        return CsvParser.encode(paymentId, transactionId, Double.toString(amount), method, status,
                Double.toString(amountReceived), Double.toString(changeAmount), reference,
                createdAt == null ? "" : createdAt.toString());
    }

    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() >= 9) {
            paymentId = values.get(0);
            transactionId = values.get(1);
            amount = Double.parseDouble(values.get(2));
            method = values.get(3);
            status = values.get(4);
            amountReceived = Double.parseDouble(values.get(5));
            changeAmount = Double.parseDouble(values.get(6));
            reference = values.get(7);
            createdAt = LocalDateTime.parse(values.get(8));
        } else if (values.size() >= 4) {
            paymentId = values.get(0);
            transactionId = "";
            amount = Double.parseDouble(values.get(1));
            method = values.get(2);
            status = values.get(3);
            amountReceived = amount;
            changeAmount = 0;
            reference = "";
            createdAt = LocalDateTime.now();
        }
    }
}
