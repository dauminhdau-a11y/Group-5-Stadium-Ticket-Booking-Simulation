package com.ticketbooking.model;

public class Voucher extends BaseEntity {
    private String code;
    private double discountPercent;
    private double discountAmount;
    private boolean used;

    public Voucher() {
        super();
    }

    public Voucher(String code, double discountPercent, double discountAmount, boolean used) {
        super(code);
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.used = used;
    }

    public String getCode() {
        return code != null ? code : getId();
    }

    public void setCode(String code) {
        this.code = code;
        setId(code);
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public double calculateDiscount(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative.");
        }
        double discount = discountPercent > 0
                ? subtotal * discountPercent / 100.0 : discountAmount;
        return Math.min(subtotal, Math.max(0, discount));
    }

    @Override
    public String toCsvLine() {
        return getCode() + "," + discountPercent + "," + discountAmount + "," + used;
    }

    public Voucher fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid voucher CSV data.");
        }
        this.code = parts[0].trim();
        this.discountPercent = Double.parseDouble(parts[1].trim());
        this.discountAmount = Double.parseDouble(parts[2].trim());
        this.used = Boolean.parseBoolean(parts[3].trim());
        setId(code);
        return this;
    }
}