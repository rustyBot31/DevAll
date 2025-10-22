package com.vasubhakt.DevAllAuthService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Document(collection = "users")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String username;

    @Email
    @Indexed(unique = true)
    private String email;

    @NonNull
    private String password;

    private boolean enabled = false;
    
    private String role = "USER";

    private String verificationToken;

}
