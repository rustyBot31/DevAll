package com.vasubhakt.DevAllCPService.Repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

@Repository
public interface CpProfileRepo extends MongoRepository<CpProfile, String> {
    Optional<CpProfile> findByUserId(String userId);
}
