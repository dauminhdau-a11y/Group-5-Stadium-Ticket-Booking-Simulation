package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.util.CsvParser;

public class Voucher extends SimpleEntity {
    private String voucherId;
    private String code;
    private double discountValue;
    private LocalDateTime validDate;
    private int usageLimit;
    private int usedCount;

    public Voucher() { }

    public Voucher(String voucherId, String code, double discountValue, LocalDateTime validDate) {
        this(voucherId, code, discountValue, validDate, 500);
    }

    public Voucher(String voucherId, String code, double discountValue, LocalDateTime validDate, int usageLimit) {
        if (usageLimit < 1) {
            throw new IllegalArgumentException("Voucher usage limit must be positive.");
        }
        this.voucherId = voucherId;
        this.code = code;
        this.discountValue = discountValue;
        this.validDate = validDate;
        this.usageLimit = usageLimit;
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
    public int getUsageLimit() { return usageLimit; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = usageLimit; }
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = Math.max(0, usedCount); }
    public int getRemainingUses() { return Math.max(0, usageLimit - usedCount); }
    public boolean hasRemainingUses() { return getRemainingUses() > 0; }
    public boolean isUsed() { return !hasRemainingUses(); }
    public void setUsed(boolean used) { usedCount = used ? usageLimit : 0; }
    public boolean recordUse() {
        if (!hasRemainingUses()) {
            return false;
        }
        usedCount++;
        return true;
    }
    public void releaseUse() {
        if (usedCount > 0) {
            usedCount--;
        }
    }
    public boolean createVoucher() { return false; }
    public boolean updateVoucher() { return false; }
    public boolean removeVoucher() { return false; }
    @Override public String toCsvLine() {
        return CsvParser.encode(voucherId, code, Double.toString(discountValue),
                validDate == null ? "" : validDate.toString(), Integer.toString(usageLimit),
                Integer.toString(usedCount));
    }
    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        voucherId = values.get(0); code = values.get(1); discountValue = Double.parseDouble(values.get(2));
        validDate = LocalDateTime.parse(values.get(3));
        if (values.size() >= 6) {
            usageLimit = Integer.parseInt(values.get(4));
            usedCount = Integer.parseInt(values.get(5));
        } else {
            usageLimit = 500;
            usedCount = values.size() > 4 && Boolean.parseBoolean(values.get(4)) ? 1 : 0;
        }
    }
}
