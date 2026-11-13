package model;

public class Voucher extends BaseEntity {
    private String code;
    private double discountPercent;
    private double discountAmount;
    private double minimumOrderAmount = 200000.0;
    private boolean used;

    public Voucher() {
        super();
    }

    public Voucher(String code, double discountPercent, double discountAmount, boolean used) {
        this(code, discountPercent, discountAmount, 200000.0, used);
    }

    public Voucher(String code, double discountPercent, double discountAmount,
            double minimumOrderAmount, boolean used) {
        super(code);
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        setMinimumOrderAmount(minimumOrderAmount);
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

    public double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(double minimumOrderAmount) {
        if (!Double.isFinite(minimumOrderAmount) || minimumOrderAmount < 0) {
            throw new IllegalArgumentException("Minimum order amount must be finite and non-negative.");
        }
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isApplicableTo(double subtotal) {
        return Double.isFinite(subtotal) && subtotal >= minimumOrderAmount;
    }

    public double calculateDiscount(double subtotal) {
        if (!Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException("Subtotal must be finite and non-negative.");
        }
        if (!isApplicableTo(subtotal)) {
            throw new IllegalArgumentException(String.format(
                    "Order must be at least %,.0f VND to use this voucher.",
                    minimumOrderAmount));
        }
        double discount = discountPercent > 0
                ? subtotal * discountPercent / 100.0 : discountAmount;
        return Math.min(subtotal, Math.max(0, discount));
    }

    @Override
    public String toCsvLine() {
        return getCode() + "," + discountPercent + "," + discountAmount + ","
                + minimumOrderAmount + "," + used;
    }

    public Voucher fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4 && parts.length != 5) {
            throw new IllegalArgumentException("Invalid voucher CSV data.");
        }
        this.code = parts[0].trim();
        this.discountPercent = Double.parseDouble(parts[1].trim());
        this.discountAmount = Double.parseDouble(parts[2].trim());
        if (parts.length == 5) {
            setMinimumOrderAmount(Double.parseDouble(parts[3].trim()));
            this.used = Boolean.parseBoolean(parts[4].trim());
        } else {
            this.minimumOrderAmount = 200000.0;
            this.used = Boolean.parseBoolean(parts[3].trim());
        }
        setId(code);
        return this;
    }
}