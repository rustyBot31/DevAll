package com.vasubhakt.DevAllAuthService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vasubhakt.DevAllAuthService.dto.LoginRequest;
import com.vasubhakt.DevAllAuthService.dto.LoginResponse;
import com.vasubhakt.DevAllAuthService.dto.ResetPasswordRequest;
import com.vasubhakt.DevAllAuthService.dto.SignupRequest;
import com.vasubhakt.DevAllAuthService.dto.SignupResponse;
import com.vasubhakt.DevAllAuthService.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostConstruct
    public void init() {
        System.out.println("AuthController bean created!");
    }

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.register(request);
            return ResponseEntity.ok(response.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // EMAIL VERIFICATION
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            String message = authService.verifyUser(token);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //RESEND VERIFICATION EMAIL
    @GetMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam("email") String email) {
        try {
            String message = authService.resendVerificationEmail(email);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //DELETE 
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("username") String username) {
        try {
            String message = authService.deleteUser(username);
            return ResponseEntity.ok(message);
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //FORGOT PASSWORD
    @PostMapping("/forgotPass")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            String message = authService.forgotPassword(email);
            return ResponseEntity.ok(message);
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //RESET PASSWORD
    @PostMapping("/resetPass")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest request) {
        try {
            String newPassword = request.getNewPassword();
            String message = authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(message);
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
