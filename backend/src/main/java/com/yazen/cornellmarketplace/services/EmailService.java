package com.yazen.cornellmarketplace.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.verification.code-expiry-minutes}")
    private long codeExpiryMinutes;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Verify your Cornell Marketplace account");
        message.setText(
                "Welcome to Cornell Marketplace!\n\n" +
                "Your verification code is: " + code + "\n\n" +
                "This code expires in " + codeExpiryMinutes + " minutes. " +
                "Enter it on the verification page to activate your account.\n\n" +
                "If you didn't request this, you can safely ignore this email."
        );
        mailSender.send(message);
    }
}
