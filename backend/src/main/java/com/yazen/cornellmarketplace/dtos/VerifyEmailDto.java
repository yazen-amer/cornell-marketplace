package com.yazen.cornellmarketplace.dtos;

import jakarta.validation.constraints.*;

public class VerifyEmailDto {
    @NotBlank @Size(max = 100)
    private String email;
    @NotBlank @Pattern(regexp = "[0-9]{6}")
    private String code;

    public VerifyEmailDto() {}

    public VerifyEmailDto(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
}
