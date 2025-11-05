package com.vasubhakt.DevAllProjectService.Service;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;

public interface ProjectService {
    ProjectProfile getUserProfile(String username);
    void createUserProfile(String username);
   //  ProjectProfile updateUserProfile(String username, ProjectProfile updatedProfile); //For testing purpose only
    ProjectProfile deleteUserProfile(String username);
}
