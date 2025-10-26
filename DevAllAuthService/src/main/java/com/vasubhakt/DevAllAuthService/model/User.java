package com.vasubhakt.DevAllAuthService.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Document(collection = "users")
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    @Email
    @Indexed(unique = true)
    private String email;

    private String password;

    private boolean enabled = false;
    
    private String role = "USER";

    private String verificationToken;

}
