package com.yazen.cornellmarketplace.dtos;

import jakarta.validation.constraints.*;

public class ResendVerificationDto {
    @NotBlank @Size(max = 100)
    private String email;

    public ResendVerificationDto() {}

    public ResendVerificationDto(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
