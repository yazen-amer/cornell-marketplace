package com.yazen.cornellmarketplace.dtos;

import jakarta.validation.constraints.*;

public class LoginDto {
    @NotBlank @Size(max = 100)
    private String email;
    @NotBlank @Size(max = 72)
    private String password;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}