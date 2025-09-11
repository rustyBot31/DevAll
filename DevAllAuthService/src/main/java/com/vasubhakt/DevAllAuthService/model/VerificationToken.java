package com.vasubhakt.DevAllAuthService.model;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "verification_tokens")
public class VerificationToken {

    @Id
    private ObjectId id;

    private String token;

    private ObjectId userId;

    private LocalDateTime expiryDate;
    
}
