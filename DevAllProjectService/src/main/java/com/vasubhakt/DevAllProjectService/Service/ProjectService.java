package com.vasubhakt.DevAllProjectService.Service;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;

public interface ProjectService {
    ProjectProfile getUserProfile(String username);
    void createUserProfile(String username);
    ProjectProfile updateUserProfile(String username, ProjectProfile updatedProfile);
    ProjectProfile deleteUserProfile(String username);
}
