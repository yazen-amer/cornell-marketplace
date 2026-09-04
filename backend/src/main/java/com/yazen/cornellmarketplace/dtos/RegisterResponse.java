package com.yazen.cornellmarketplace.dtos;

public class RegisterResponse {

    private String message;
    private String email;

    public RegisterResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}
