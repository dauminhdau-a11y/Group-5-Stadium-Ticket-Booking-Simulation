package com.ticketbooking.exception;

public class VoucherInvalidException extends AppException {
    public VoucherInvalidException() {
        super("The voucher code is invalid or has expired.", "VOUCHER_INVALID");
    }

    public VoucherInvalidException(String message) {
        super(message, "VOUCHER_INVALID");
    }
}
