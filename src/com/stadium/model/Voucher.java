package com.stadium.model;

import java.time.LocalDateTime;

public class Voucher extends SimpleEntity {
    private String voucherId;
    private String code;
    private double discountValue;
    private LocalDateTime validDate;
    public Voucher() { }
    public Voucher(String voucherId, String code, double discountValue, LocalDateTime validDate) {
        this.voucherId = voucherId; this.code = code;
        this.discountValue = discountValue; this.validDate = validDate;
    }
    @Override public String getId() { return voucherId; }
    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public LocalDateTime getValidDate() { return validDate; }
    public void setValidDate(LocalDateTime validDate) { this.validDate = validDate; }
    public boolean createVoucher() { return false; }
    public boolean updateVoucher() { return false; }
    public boolean removeVoucher() { return false; }
    @Override public String toCsvLine() { return voucherId + "," + code + "," + discountValue + "," + validDate; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        voucherId = values[0]; code = values[1]; discountValue = Double.parseDouble(values[2]);
        validDate = LocalDateTime.parse(values[3]);
    }
}
