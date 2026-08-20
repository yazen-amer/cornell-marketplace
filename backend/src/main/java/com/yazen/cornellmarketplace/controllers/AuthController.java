package com.yazen.cornellmarketplace.controllers;

import com.yazen.cornellmarketplace.dtos.LoginResponse;
import com.yazen.cornellmarketplace.dtos.RegisterDto;
import com.yazen.cornellmarketplace.dtos.LoginDto;
import com.yazen.cornellmarketplace.dtos.RegisterResponse;
import com.yazen.cornellmarketplace.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public RegisterResponse registerUser(@RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public LoginResponse loginUser(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }



}
