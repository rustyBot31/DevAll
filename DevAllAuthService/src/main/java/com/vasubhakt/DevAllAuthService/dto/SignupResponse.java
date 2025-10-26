package com.vasubhakt.DevAllAuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {
    private String username;
    private String email;
    private String message;
}
