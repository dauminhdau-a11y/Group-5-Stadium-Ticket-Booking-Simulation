package com.stadium.model;

import com.stadium.model.enums.BookingStatus;

public class Booking extends SimpleEntity {
    private BookingStatus status = BookingStatus.PENDING;
    public Booking() { }
    public Booking(String id) { super(id); }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
