package com.ticketbooking.exception;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException() {
        super("Invalid username or password.", "INVALID_CREDENTIALS");
    }

    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
}