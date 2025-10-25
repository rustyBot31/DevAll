package com.vasubhakt.DevAllAuthService.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllAuthService.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
