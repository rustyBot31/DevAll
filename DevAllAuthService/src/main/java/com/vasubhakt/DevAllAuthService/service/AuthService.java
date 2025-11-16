package com.vasubhakt.DevAllAuthService.service;

import com.vasubhakt.DevAllAuthService.dto.LoginRequest;
import com.vasubhakt.DevAllAuthService.dto.LoginResponse;
import com.vasubhakt.DevAllAuthService.dto.SignupRequest;
import com.vasubhakt.DevAllAuthService.dto.SignupResponse;

public interface AuthService {

    SignupResponse register(SignupRequest request);
    String verifyUser(String token);
    LoginResponse login(LoginRequest request);
    String resendVerificationEmail(String email);
    String deleteUser(String username);
}
