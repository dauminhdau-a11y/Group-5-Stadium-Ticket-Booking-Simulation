package com.stadium.model;

import java.time.LocalDateTime;

public class Booking extends SimpleEntity {
    private String bookingId;
    private LocalDateTime bookingDate;
    private double totalAmount;
    private String status;
    public Booking() { }
    public Booking(String bookingId, LocalDateTime bookingDate, double totalAmount, String status) {
        this.bookingId = bookingId; this.bookingDate = bookingDate;
        this.totalAmount = totalAmount; this.status = status;
    }
    @Override public String getId() { return bookingId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean createBooking() { return false; }
    public boolean cancelBooking() { return false; }
    @Override public String toCsvLine() { return bookingId + "," + bookingDate + "," + totalAmount + "," + status; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        bookingId = values[0]; bookingDate = LocalDateTime.parse(values[1]);
        totalAmount = Double.parseDouble(values[2]); status = values[3];
    }
}
