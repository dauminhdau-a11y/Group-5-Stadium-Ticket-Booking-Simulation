package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.model.enums.BookingStatus;
import com.util.CsvParser;

public class BookingTransaction extends SimpleEntity {
    private String transactionId;
    private String fanId;
    private String staffId;
    private String matchId;
    private String voucherId;
    private String buyerName;
    private String buyerContact;
    private double originalAmount;
    private double discountAmount;
    private double finalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;

    public BookingTransaction() {
        this.bookingDate = LocalDateTime.now();
    }

    public BookingTransaction(String transactionId, String fanId, String staffId, String matchId,
                              String voucherId, double originalAmount, double discountAmount,
                              double finalAmount, LocalDateTime bookingDate, BookingStatus status) {
        this.transactionId = transactionId;
        this.fanId = fanId;
        this.staffId = staffId;
        this.matchId = matchId;
        this.voucherId = voucherId;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.bookingDate = bookingDate == null ? LocalDateTime.now() : bookingDate;
        this.status = status;
    }

    @Override public String getId() { return transactionId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerContact() { return buyerContact; }
    public void setBuyerContact(String buyerContact) { this.buyerContact = buyerContact; }
    public double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(double originalAmount) { this.originalAmount = originalAmount; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    @Override public String toCsvLine() {
        return CsvParser.encode(transactionId, fanId, staffId, matchId, voucherId,
                Double.toString(originalAmount), Double.toString(discountAmount), Double.toString(finalAmount),
            bookingDate == null ? "" : bookingDate.toString(), status == null ? "" : status.name(),
            buyerName, buyerContact);
    }

    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() < 10) return;
        transactionId = values.get(0);
        fanId = values.get(1);
        staffId = values.get(2);
        matchId = values.get(3);
        voucherId = values.get(4);
        originalAmount = Double.parseDouble(values.get(5));
        discountAmount = Double.parseDouble(values.get(6));
        finalAmount = Double.parseDouble(values.get(7));
        bookingDate = LocalDateTime.parse(values.get(8));
        status = BookingStatus.valueOf(values.get(9));
        buyerName = values.size() > 10 ? values.get(10) : "";
        buyerContact = values.size() > 11 ? values.get(11) : "";
    }
}
