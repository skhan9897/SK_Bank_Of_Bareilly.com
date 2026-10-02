package com.skbank.exception;

public class UnauthorizedAccessException extends BankException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
