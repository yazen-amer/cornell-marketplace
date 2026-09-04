package com.yazen.cornellmarketplace.dtos;

public class VerifyEmailDto {
    private String email;
    private String code;

    public VerifyEmailDto() {}

    public VerifyEmailDto(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
}
