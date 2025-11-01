package com.vasubhakt.DevAllProjectService.Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProjectProfile {
    
    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = true)
    private String username;

    private GitHubProfile gitHubProfile;

}
