package com.stadium.com.exception;

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) { super(message); }
}
