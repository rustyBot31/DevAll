package com.vasubhakt.DevAllAuthService.serviceImpl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllAuthService.client.CPClient;
import com.vasubhakt.DevAllAuthService.dto.LoginRequest;
import com.vasubhakt.DevAllAuthService.dto.LoginResponse;
import com.vasubhakt.DevAllAuthService.model.User;
import com.vasubhakt.DevAllAuthService.repo.UserRepository;
import com.vasubhakt.DevAllAuthService.security.JwtUtil;
import com.vasubhakt.DevAllAuthService.service.AuthService;
import com.vasubhakt.DevAllAuthService.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final CPClient cpClient;

    @Override
    public String register(User user) {
        if(userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getUsername() + " already exists");
        }
        if(userRepo.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepo.save(user);

        String link = token;
        emailService.sendVerificationEmail(user.getEmail(), link);
        return "User registered successfully! Please check your email to verify your account.";
    }

    @Override
    public String verifyUser(String token) {
        Optional<User> optionalUser = userRepo.findByVerificationToken(token);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid verification token");
        }
        User user = optionalUser.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);
        cpClient.createCPProfile(user.getUsername());
        return "User verified successfully!";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepo.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if(!passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        User user = optionalUser.get();
        if(!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please check your email.");
        }
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
}
