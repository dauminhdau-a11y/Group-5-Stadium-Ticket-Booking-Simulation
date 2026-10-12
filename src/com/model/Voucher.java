package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.util.CsvParser;

public class Voucher extends SimpleEntity {
    private String voucherId;
    private String code;
    private double discountValue;
    private LocalDateTime validDate;
    private boolean used;
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
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public boolean createVoucher() { return false; }
    public boolean updateVoucher() { return false; }
    public boolean removeVoucher() { return false; }
    @Override public String toCsvLine() {
        return CsvParser.encode(voucherId, code, Double.toString(discountValue),
                validDate == null ? "" : validDate.toString(), Boolean.toString(used));
    }
    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        voucherId = values.get(0); code = values.get(1); discountValue = Double.parseDouble(values.get(2));
        validDate = LocalDateTime.parse(values.get(3));
        used = values.size() > 4 && Boolean.parseBoolean(values.get(4));
    }
}
