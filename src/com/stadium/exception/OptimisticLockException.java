package com.stadium.exception;

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) { super(message); }
}
