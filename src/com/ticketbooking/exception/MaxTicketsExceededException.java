package com.ticketbooking.exception;

public class MaxTicketsExceededException extends AppException {
    public MaxTicketsExceededException() {
        super("You have exceeded maximum allowed tickets for this booking.", "MAX_TICKETS_EXCEEDED");
    }

    public MaxTicketsExceededException(String message) {
        super(message, "MAX_TICKETS_EXCEEDED");
    }
}
