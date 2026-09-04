package com.yazen.cornellmarketplace.configs;

import com.yazen.cornellmarketplace.exceptions.AlreadyVerifiedException;
import com.yazen.cornellmarketplace.exceptions.EmailAlreadyRegisteredException;
import com.yazen.cornellmarketplace.exceptions.InvalidCornellEmailException;
import com.yazen.cornellmarketplace.exceptions.InvalidVerificationCodeException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCornellEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmail(InvalidCornellEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(EmailAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCode(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AlreadyVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyVerified(AlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    // Thrown by Spring Security's DaoAuthenticationProvider when Users.isEnabled()
    // returns false — i.e. the account hasn't verified its @cornell.edu email yet.
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Please verify your Cornell email before logging in."));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
    }
}
