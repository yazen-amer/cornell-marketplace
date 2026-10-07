package com.yazen.cornellmarketplace.dtos;

import jakarta.validation.constraints.*;

public class RegisterDto {
    @NotBlank @Size(max = 100)
    private String username;
    @NotBlank @Size(max = 100)
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String password;

    public RegisterDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}