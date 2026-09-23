package com.stadium.model;

public class Payment extends SimpleEntity {
    private String paymentId;
    private double amount;
    private String method;
    private String status;
    public Payment() { }
    public Payment(String paymentId, double amount, String method, String status) {
        this.paymentId = paymentId; this.amount = amount; this.method = method; this.status = status;
    }
    @Override public String getId() { return paymentId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean processPayment() { return false; }
    public boolean processRefund() { return false; }
    @Override public String toCsvLine() { return paymentId + "," + amount + "," + method + "," + status; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        paymentId = values[0]; amount = Double.parseDouble(values[1]); method = values[2]; status = values[3];
    }
}
