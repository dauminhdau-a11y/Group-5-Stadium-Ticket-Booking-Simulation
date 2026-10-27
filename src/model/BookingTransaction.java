package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingTransaction extends BaseEntity {
    private String transactionId;
    private String fanId;
    private String matchId;
    private double totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private List<String> seatIds = new ArrayList<>();
    private PaymentMethod paymentMethod = PaymentMethod.QR_CODE;
    private String voucherCode = "";
    private double discountAmount = 0.0;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BookingTransaction() {
    }

    public BookingTransaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime bookingDate, BookingStatus status) {
        this(transactionId, fanId, matchId, totalAmount, bookingDate, status, new ArrayList<>());
    }

    public BookingTransaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime bookingDate, BookingStatus status, List<String> seatIds) {
        this(transactionId, fanId, matchId, totalAmount, bookingDate, status, seatIds, PaymentMethod.QR_CODE, "", 0.0);
    }

    public BookingTransaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime bookingDate, BookingStatus status, List<String> seatIds,
            PaymentMethod paymentMethod, String voucherCode, double discountAmount) {
        this.transactionId = transactionId;
        this.fanId = fanId;
        this.matchId = matchId;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.status = status;
        this.seatIds = seatIds;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String toCsvLine() {
        String seatListStr = String.join(";", seatIds != null ? seatIds : new ArrayList<>());
        return transactionId + "," + fanId + "," + matchId + "," + totalAmount +
                "," + bookingDate.format(DATE_TIME_FORMATTER) + "," + status.name() +
                "," + (paymentMethod != null ? paymentMethod.name() : PaymentMethod.QR_CODE.name()) +
                "," + (voucherCode != null ? voucherCode : "") +
                "," + discountAmount +
                "," + seatListStr;
    }

    @Override
    public void fromCsvLine(String csvLine) {
        if (csvLine != null && !csvLine.trim().isEmpty()) {
            String[] parts = csvLine.split(",");
            if (parts.length >= 6) {
                this.transactionId = parts[0].trim();
                this.fanId = parts[1].trim();
                this.matchId = parts[2].trim();
                this.totalAmount = Double.parseDouble(parts[3].trim());
                this.bookingDate = LocalDateTime.parse(parts[4].trim(), DATE_TIME_FORMATTER);
                this.status = BookingStatus.valueOf(parts[5].trim());
            }
            if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                try {
                    this.paymentMethod = PaymentMethod.valueOf(parts[6].trim());
                } catch (IllegalArgumentException e) {
                    this.paymentMethod = PaymentMethod.QR_CODE;
                }
            }
            if (parts.length >= 8) {
                this.voucherCode = parts[7].trim();
            }
            if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                try {
                    this.discountAmount = Double.parseDouble(parts[8].trim());
                } catch (NumberFormatException ignored) {
                }
            }
            if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
                this.seatIds = Arrays.asList(parts[9].trim().split(";"));
            }
        }
    }

    public String getFanId() {
        return fanId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getId() {
        return transactionId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
}
