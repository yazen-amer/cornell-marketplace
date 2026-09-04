package com.yazen.cornellmarketplace.exceptions;

public class AlreadyVerifiedException extends RuntimeException {
    public AlreadyVerifiedException() {
        super("This account is already verified. You can log in.");
    }
}
