package com.yazen.cornellmarketplace.dtos;

public class RegisterDto {
    private String username;
    private String email;
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