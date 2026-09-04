package com.yazen.cornellmarketplace.dtos;

public class ResendVerificationDto {
    private String email;

    public ResendVerificationDto() {}

    public ResendVerificationDto(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
