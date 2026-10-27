package com.ticketbooking.model;

import com.ticketbooking.model.enums.PaymentMethod;
import com.ticketbooking.model.enums.TransactionStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction extends BaseEntity {
    private String transactionId;
    private String fanId;
    private String matchId;
    private double totalAmount;
    private LocalDateTime transactionDate;
    private TransactionStatus status;
    private List<String> seatIds = new ArrayList<>();
    private PaymentMethod paymentMethod = PaymentMethod.QR_CODE;
    private String voucherCode = "";
    private double discountAmount = 0.0;
    private DateTimeFormatter dateTimeFormatter;

    public Transaction() {
        super();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public Transaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime transactionDate, TransactionStatus status) {
        this(transactionId, fanId, matchId, totalAmount, transactionDate, status, new ArrayList<>());
    }

    public Transaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime transactionDate, TransactionStatus status, List<String> seatIds) {
        this(transactionId, fanId, matchId, totalAmount, transactionDate, status, seatIds,
                PaymentMethod.QR_CODE, "", 0.0);
    }

    public Transaction(String transactionId, String fanId, String matchId, double totalAmount,
            LocalDateTime transactionDate, TransactionStatus status, List<String> seatIds,
            PaymentMethod paymentMethod, String voucherCode, double discountAmount) {
        super(transactionId);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.transactionId = transactionId;
        this.fanId = fanId;
        this.matchId = matchId;
        this.totalAmount = totalAmount;
        this.transactionDate = transactionDate;
        this.status = status;
        this.seatIds = seatIds == null ? new ArrayList<>() : new ArrayList<>(seatIds);
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
    }

    public String getTransactionId() {
        return transactionId != null ? transactionId : getId();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        setId(transactionId);
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public List<String> getSeatIds() {
        return new ArrayList<>(seatIds);
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds == null ? new ArrayList<>() : new ArrayList<>(seatIds);
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

    public DateTimeFormatter getDateTimeFormatter() {
        if (dateTimeFormatter == null) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        return dateTimeFormatter;
    }

    @Override
    public String toCsvLine() {
        String seatListStr = String.join(";", seatIds != null ? seatIds : new ArrayList<>());
        String dateStr = transactionDate != null ? transactionDate.format(getDateTimeFormatter()) : "";
        return getTransactionId() + "," + (fanId != null ? fanId : "") + ","
                + (matchId != null ? matchId : "") + "," + totalAmount + ","
                + dateStr + ","
                + (status != null ? status.name() : TransactionStatus.PENDING.name()) + ","
                + (paymentMethod != null ? paymentMethod.name() : PaymentMethod.QR_CODE.name()) + ","
                + (voucherCode != null ? voucherCode : "") + ","
                + discountAmount + ","
                + seatListStr;
    }

    public Transaction fromCsvLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction CSV data cannot be empty.");
        }
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid transaction CSV data.");
        }
        this.transactionId = parts[0].trim();
        setId(this.transactionId);
        this.fanId = parts[1].trim();
        this.matchId = parts[2].trim();
        this.totalAmount = Double.parseDouble(parts[3].trim());
        this.transactionDate = LocalDateTime.parse(parts[4].trim(), getDateTimeFormatter());
        this.status = TransactionStatus.valueOf(parts[5].trim());
        if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
            this.paymentMethod = PaymentMethod.valueOf(parts[6].trim());
        }
        if (parts.length >= 8) {
            this.voucherCode = parts[7].trim();
        }
        if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
            this.discountAmount = Double.parseDouble(parts[8].trim());
        }
        if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
            this.seatIds = Arrays.asList(parts[9].trim().split(";"));
        }
        return this;
    }
}
