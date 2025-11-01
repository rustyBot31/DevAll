package com.vasubhakt.DevAllProjectService.Repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;

public interface ProjectRepo extends MongoRepository<ProjectProfile, ObjectId> {
    
}
