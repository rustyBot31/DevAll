package com.vasubhakt.DevAllCPService.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

public interface CpProfileRepo extends MongoRepository<CpProfile, String> {
    CpProfile findByUserId(String userId);
}
