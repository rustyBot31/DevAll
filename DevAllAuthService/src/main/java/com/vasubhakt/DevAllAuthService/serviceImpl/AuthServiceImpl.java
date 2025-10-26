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
import com.vasubhakt.DevAllAuthService.dto.SignupRequest;
import com.vasubhakt.DevAllAuthService.dto.SignupResponse;
import com.vasubhakt.DevAllAuthService.model.User;
import com.vasubhakt.DevAllAuthService.repo.UserRepository;
import com.vasubhakt.DevAllAuthService.security.JwtUtil;
import com.vasubhakt.DevAllAuthService.service.AuthService;
import com.vasubhakt.DevAllAuthService.service.EmailService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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
    @CircuitBreaker(name = "CPBreaker", fallbackMethod = "cpFallback")
    @Retry(name = "CPRetry", fallbackMethod = "cpFallback")
    @RateLimiter(name = "CPRateLimiter", fallbackMethod = "cpFallback")
    public SignupResponse register(SignupRequest request) {
        if(userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email already exists");
        }
        if(userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User with username already exists");
        }
        if(request.getEmail()==null || request.getUsername()==null || request.getPassword()==null || request.getEmail().isEmpty() || request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            throw new RuntimeException("Neccessary fields missing");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepo.save(user);

        String link = token;
        emailService.sendVerificationEmail(user.getEmail(), link);
        SignupResponse response = new SignupResponse(user.getUsername(), user.getEmail(), "User registered successfully! Please check your mail for verification");
        return response;
    }

    @Override
    @CircuitBreaker(name = "CPBreaker", fallbackMethod = "cpFallback")
    @Retry(name = "CPRetry", fallbackMethod = "cpFallback")
    @RateLimiter(name = "CPRateLimiter", fallbackMethod = "cpFallback")
    public String verifyUser(String token) {
        Optional<User> optionalUser = userRepo.findByVerificationToken(token);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid verification token");
        }
        User user = optionalUser.get();
        cpClient.createCPProfile(user.getUsername());
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);
        
        return "User verified successfully!";
    }

    @Override
    @CircuitBreaker(name = "CPBreaker", fallbackMethod = "cpFallback")
    @Retry(name = "CPRetry", fallbackMethod = "cpFallback")
    @RateLimiter(name = "CPRateLimiter", fallbackMethod = "cpFallback")
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

    public String cpFallback(Exception e) {
        return e.getMessage();
    }

}
