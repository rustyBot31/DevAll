package com.vasubhakt.DevAllCPService.Repo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

@Repository
public interface CpProfileRepo extends MongoRepository<CpProfile, ObjectId> {
    Optional<CpProfile> findByUsername(String username);
}
