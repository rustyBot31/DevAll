package com.vasubhakt.DevAllAuthService.controller;

import java.util.Optional;
import java.util.UUID;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.vasubhakt.DevAllAuthService.dto.LoginRequest;
import com.vasubhakt.DevAllAuthService.dto.LoginResponse;
import com.vasubhakt.DevAllAuthService.model.User;
import com.vasubhakt.DevAllAuthService.repo.UserRepository;
import com.vasubhakt.DevAllAuthService.security.JwtUtil;
import com.vasubhakt.DevAllAuthService.service.EmailService;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        System.out.println("AuthController bean created!");
    }

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        System.out.println("SIGNUP endpoint hit!");
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        String link =  verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), link);

        return ResponseEntity.ok("User registered successfully. Please check your email to verify your account.");
    }

    // EMAIL VERIFICATION
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }

        User user = optionalUser.get();
        user.setEnabled(true);
        user.setVerificationToken(null); // clear after verification
        userRepository.save(user);

        return ResponseEntity.ok("User verified successfully. You can now log in.");
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Invalid email or password");
        }
    
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).body("Account not verified. Please check your email.");
        }
    
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    
        String token = jwtUtil.generateToken(user.getEmail());
    
        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole()));
    }
    
}
