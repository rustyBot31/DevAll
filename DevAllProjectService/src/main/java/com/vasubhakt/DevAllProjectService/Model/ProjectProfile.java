package com.vasubhakt.DevAllProjectService.Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Document(collection = "projectProfiles")
public class ProjectProfile {
    
    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = true)
    private String username;

    private GitHubProfile gitHubProfile;
    private HuggingFaceProfile huggingFaceProfile;
    private GitLabProfile gitLabProfile;

}
