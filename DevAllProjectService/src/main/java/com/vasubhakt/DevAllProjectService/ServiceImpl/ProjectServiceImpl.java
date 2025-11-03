package com.vasubhakt.DevAllProjectService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;
import com.vasubhakt.DevAllProjectService.Service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    
    @Override
    public ProjectProfile getUserProfile(String username) {
        return projectRepo.findByUsername(username).
                    orElseThrow(() -> new RuntimeException("User Project Profile not found"));
    }

    @Override
    public void createUserProfile(String username) {
        ProjectProfile profile = new ProjectProfile();
        profile.setUsername(username);
        projectRepo.save(profile);
    }

    @Override
    public ProjectProfile updateUserProfile(String username, ProjectProfile updatedProfile) {
        ProjectProfile existingProfile = projectRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Project Profile not found"));
        existingProfile.setGitHubProfile(updatedProfile.getGitHubProfile());
        projectRepo.save(existingProfile);
        return (existingProfile);
    }

    @Override
    public ProjectProfile deleteUserProfile(String username) {
        ProjectProfile existingProfile = projectRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Project Profile not found"));
        projectRepo.delete(existingProfile);
        return existingProfile;
    }
        
}
