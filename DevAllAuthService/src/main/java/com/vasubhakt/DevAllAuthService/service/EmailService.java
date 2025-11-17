package com.vasubhakt.DevAllAuthService.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendResetPasswordEmail(String email, String token);
}
