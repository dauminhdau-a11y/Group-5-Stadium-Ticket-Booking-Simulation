package com.ticketbooking.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(){
        super("This account is locked.");
    }
}