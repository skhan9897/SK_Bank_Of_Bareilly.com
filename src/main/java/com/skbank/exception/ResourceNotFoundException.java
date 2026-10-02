package com.skbank.exception;

public class ResourceNotFoundException extends BankException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
