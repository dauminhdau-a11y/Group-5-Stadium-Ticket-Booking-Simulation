package com.ticketbooking.exception;

public class CsvParseException extends AppException {
    public CsvParseException(String message) {
        super(message, "CSV_PARSE_ERROR");
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
