package com.vasubhakt.DevAllProjectService.Repo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;

public interface ProjectRepo extends MongoRepository<ProjectProfile, ObjectId> {
    Optional<ProjectProfile> findByUsername(String username);
}
