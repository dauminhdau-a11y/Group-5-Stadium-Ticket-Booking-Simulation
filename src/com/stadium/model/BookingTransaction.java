package com.stadium.model;

import com.stadium.model.enums.BookingStatus;
import java.time.LocalDateTime;

public class BookingTransaction extends SimpleEntity {
    private String transactionId;
    private String fanId;
    private String matchId;
    private double totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status = BookingStatus.PENDING;

    public BookingTransaction() { }
    public BookingTransaction(String transactionId, String fanId, String matchId, double totalAmount,
                              LocalDateTime bookingDate, BookingStatus status) {
        this.transactionId = transactionId; this.fanId = fanId; this.matchId = matchId;
        this.totalAmount = totalAmount; this.bookingDate = bookingDate; this.status = status;
    }
    @Override public String getId() { return transactionId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String value) { transactionId = value; }
    public String getFanId() { return fanId; }
    public void setFanId(String value) { fanId = value; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String value) { matchId = value; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double value) { totalAmount = value; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime value) { bookingDate = value; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus value) { status = value; }
    @Override public String toCsvLine() {
        return String.join(",", value(transactionId), value(fanId), value(matchId), Double.toString(totalAmount),
                bookingDate == null ? "" : bookingDate.toString(), status.name());
    }
    @Override public void fromCsvLine(String line) {
        String[] v = line.split(",", -1);
        transactionId = v[0]; fanId = v[1]; matchId = v[2]; totalAmount = Double.parseDouble(v[3]);
        bookingDate = v[4].isEmpty() ? null : LocalDateTime.parse(v[4]); status = BookingStatus.valueOf(v[5]);
    }
    private String value(String s) { return s == null ? "" : s; }
}
