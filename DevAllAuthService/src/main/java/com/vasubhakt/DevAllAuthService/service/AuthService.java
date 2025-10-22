package com.vasubhakt.DevAllAuthService.service;

import com.vasubhakt.DevAllAuthService.dto.LoginRequest;
import com.vasubhakt.DevAllAuthService.dto.LoginResponse;
import com.vasubhakt.DevAllAuthService.model.User;

public interface AuthService {

    String register(User user);
    String verifyUser(String token);
    LoginResponse login(LoginRequest request);
    
}
