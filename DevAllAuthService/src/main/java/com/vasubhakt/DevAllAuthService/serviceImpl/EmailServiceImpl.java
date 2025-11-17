package com.vasubhakt.DevAllAuthService.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllAuthService.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;

    @Value("%{spring.mail.username}")
    private String from;

    @Value("${api-gateway.port}")
    private String serverPort;

    @Override
    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify your email - DevAll";
        String verificationUrl = "http://localhost:"+serverPort+"/auth/verify?token=" + token;

        String body = "Click the link to verify your email:\n" + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String email, String token) {
        String link = "http://localhost:"+serverPort+"/auth/resetPass?token=" + token;
        String subject = "Reset your Password - DevAll";
        String body = "Click the link to reset your password: " + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
