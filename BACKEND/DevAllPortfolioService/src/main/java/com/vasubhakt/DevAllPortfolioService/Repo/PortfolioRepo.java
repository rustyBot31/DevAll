package com.vasubhakt.DevAllPortfolioService.Repo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllPortfolioService.Model.Portfolio;

public interface PortfolioRepo extends MongoRepository<Portfolio, ObjectId> {
    Optional<Portfolio> findByUsername(String username);
}
