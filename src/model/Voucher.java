package model;

public class Voucher extends BaseEntity {
    private String code;
    private double discountPercent; // e.g. 10.0 for 10%
    private double discountAmount;  // e.g. 50000.0 for 50,000 VND
    private boolean isUsed;

    public Voucher() {
    }

    public Voucher(String code, double discountPercent, double discountAmount, boolean isUsed) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.isUsed = isUsed;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public double calculateDiscount(double originalAmount) {
        if (discountPercent > 0) {
            return originalAmount * (discountPercent / 100.0);
        }
        if (discountAmount > 0) {
            return Math.min(discountAmount, originalAmount);
        }
        return 0;
    }

    @Override
    public String toCsvLine() {
        return code + "," + discountPercent + "," + discountAmount + "," + isUsed;
    }

    @Override
    public void fromCsvLine(String csvLine) {
        if (csvLine != null && !csvLine.trim().isEmpty()) {
            String[] parts = csvLine.split(",");
            if (parts.length >= 4) {
                this.code = parts[0].trim();
                this.discountPercent = Double.parseDouble(parts[1].trim());
                this.discountAmount = Double.parseDouble(parts[2].trim());
                this.isUsed = Boolean.parseBoolean(parts[3].trim());
            }
        }
    }
}
