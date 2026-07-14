package com.yazen.cornellmarketplace.services;

import com.yazen.cornellmarketplace.dtos.LoginDto;
import com.yazen.cornellmarketplace.dtos.LoginResponse;
import com.yazen.cornellmarketplace.dtos.RegisterDto;
import com.yazen.cornellmarketplace.dtos.RegisterResponse;
import com.yazen.cornellmarketplace.entities.Users;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import com.yazen.cornellmarketplace.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse register(RegisterDto registerDto) {
        String hashedPassword = passwordEncoder.encode(registerDto.getPassword());
        Users newUser = new Users(registerDto.getUsername(), registerDto.getEmail(), hashedPassword);
        userRepository.save(newUser);
        return new RegisterResponse(jwtService.generateToken(newUser));
    }

    public LoginResponse login(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        Users user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow();
        return new LoginResponse(jwtService.generateToken(user));
    }
}
