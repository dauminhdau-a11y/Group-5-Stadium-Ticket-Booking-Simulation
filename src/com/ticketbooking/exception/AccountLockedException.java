package com.ticketbooking.exception;

public class AccountLockedException extends AppException {
    public AccountLockedException() {
        super("Your account has been locked. Please contact support.", "ACCOUNT_LOCKED");
    }

    public AccountLockedException(String message) {
        super(message, "ACCOUNT_LOCKED");
    }
}