package com.exception;

public class MaxTicketsExceededException extends RuntimeException {
    public MaxTicketsExceededException(String message) {
        super(message);
    }
}
