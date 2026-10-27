package com.ticketbooking.exception;

public class SeatAlreadyBookedException extends AppException {
    public SeatAlreadyBookedException() {
        super("This seat is already booked or reserved.", "SEAT_ALREADY_BOOKED");
    }

    public SeatAlreadyBookedException(String message) {
        super(message, "SEAT_ALREADY_BOOKED");
    }
}
