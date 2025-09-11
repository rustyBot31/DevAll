package com.vasubhakt.DevAllAuthService.repo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllAuthService.model.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String email);
    boolean existsByEmail(String email);
}
