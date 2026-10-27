package com.ticketbooking.exception;

public class OptimisticLockException extends AppException {
    public OptimisticLockException() {
        super("Concurrent modification conflict detected. Please retry.", "OPTIMISTIC_LOCK_ERROR");
    }

    public OptimisticLockException(String message) {
        super(message, "OPTIMISTIC_LOCK_ERROR");
    }
}
