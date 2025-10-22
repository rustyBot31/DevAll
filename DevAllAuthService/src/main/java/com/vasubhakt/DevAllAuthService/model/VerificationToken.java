package com.vasubhakt.DevAllAuthService.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "verification_tokens")
public class VerificationToken {

    @Id
    private String id;

    private String token;

    private String userId;

    private LocalDateTime expiryDate;
    
}
