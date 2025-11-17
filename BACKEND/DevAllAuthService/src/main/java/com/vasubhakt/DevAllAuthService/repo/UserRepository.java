package com.vasubhakt.DevAllAuthService.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllAuthService.model.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByResetPasswordToken(String token);
    void deleteAllByEnabledFalseAndCreatedAtBefore(LocalDateTime cutoff);

}
