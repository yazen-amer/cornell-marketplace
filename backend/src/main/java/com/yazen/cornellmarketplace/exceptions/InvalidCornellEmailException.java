package com.yazen.cornellmarketplace.exceptions;

public class InvalidCornellEmailException extends RuntimeException {
    public InvalidCornellEmailException(String allowedDomain) {
        super("You must register with a valid @" + allowedDomain + " email address.");
    }
}
