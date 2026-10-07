package com.yazen.cornellmarketplace.services;

import com.yazen.cornellmarketplace.dtos.LoginDto;
import com.yazen.cornellmarketplace.dtos.LoginResponse;
import com.yazen.cornellmarketplace.dtos.MessageResponse;
import com.yazen.cornellmarketplace.dtos.RegisterDto;
import com.yazen.cornellmarketplace.dtos.RegisterResponse;
import com.yazen.cornellmarketplace.dtos.ResendVerificationDto;
import com.yazen.cornellmarketplace.dtos.VerifyEmailDto;
import com.yazen.cornellmarketplace.entities.Users;
import com.yazen.cornellmarketplace.exceptions.AlreadyVerifiedException;
import com.yazen.cornellmarketplace.exceptions.EmailAlreadyRegisteredException;
import com.yazen.cornellmarketplace.exceptions.InvalidCornellEmailException;
import com.yazen.cornellmarketplace.exceptions.InvalidVerificationCodeException;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
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
    private final EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    // Which email domain counts as "Cornell-specific". Kept as a config value
    // (not hardcoded) so it can be widened later (e.g. subdomains) without a code change.
    @Value("${app.verification.allowed-email-domain}")
    private String allowedEmailDomain;

    @Value("${app.verification.code-expiry-minutes}")
    private long codeExpiryMinutes;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Transactional
    public RegisterResponse register(RegisterDto registerDto) {
        String email = normalizeEmail(registerDto.getEmail());

        if (!isCornellEmail(email)) {
            throw new InvalidCornellEmailException(allowedEmailDomain);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException();
        }

        if (registerDto.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must be at most 72 UTF-8 bytes.");
        }
        String hashedPassword = passwordEncoder.encode(registerDto.getPassword());
        Users newUser = new Users(registerDto.getUsername().trim(), email, hashedPassword);

        assignNewVerificationCode(newUser);
        userRepository.save(newUser);

        emailService.sendVerificationCode(email, newUser.getVerificationCode());

        return new RegisterResponse(
                "Account created. Check your @" + allowedEmailDomain + " inbox for a verification code.",
                email
        );
    }

    public LoginResponse login(LoginDto loginDto) {
        String email = normalizeEmail(loginDto.getEmail());
        // Spring Security calls Users.isEnabled() as part of this authenticate() call,
        // via DaoAuthenticationProvider's pre-authentication checks. An unverified
        // account throws DisabledException here, before the password is even compared.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginDto.getPassword())
        );
        Users user = userRepository.findByEmail(email).orElseThrow();
        return new LoginResponse(jwtService.generateToken(user));
    }

    @Transactional
    public LoginResponse verifyEmail(VerifyEmailDto verifyEmailDto) {
        String email = normalizeEmail(verifyEmailDto.getEmail());
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid email or code."));

        if (user.isVerified()) {
            throw new AlreadyVerifiedException();
        }

        boolean codeMatches = user.getVerificationCode() != null
                && user.getVerificationCode().equals(verifyEmailDto.getCode());
        boolean notExpired = user.getVerificationCodeExpiry() != null
                && user.getVerificationCodeExpiry().after(new Date());

        if (!codeMatches || !notExpired) {
            throw new InvalidVerificationCodeException("That code is invalid or has expired.");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        // Verification succeeded — log the user straight in.
        return new LoginResponse(jwtService.generateToken(user));
    }

    @Transactional
    public MessageResponse resendVerification(ResendVerificationDto resendDto) {
        String email = normalizeEmail(resendDto.getEmail());
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationCodeException("No account found for that email."));

        if (user.isVerified()) {
            throw new AlreadyVerifiedException();
        }

        assignNewVerificationCode(user);
        userRepository.save(user);
        emailService.sendVerificationCode(email, user.getVerificationCode());

        return new MessageResponse("A new verification code has been sent to " + email + ".");
    }

    private String normalizeEmail(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        return normalized;
    }

    private boolean isCornellEmail(String email) {
        return email.endsWith("@" + allowedEmailDomain);
    }

    private void assignNewVerificationCode(Users user) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(new Date(System.currentTimeMillis() + codeExpiryMinutes * 60_000));
    }
}
