package com.vasubhakt.DevAllAuthService.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
}
