package com.vasubhakt.DevAllProjectService.Messaging;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vasubhakt.DevAllProjectService.Model.ProjectFetchRequest;
import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;

import lombok.RequiredArgsConstructor;

@Async
@Component
@RequiredArgsConstructor
public class ProjectProfileScheduler {
    
    private final ProjectProducer projectProducer;
    private final ProjectRepo projectRepo;

    // Runs every day at 12 AM and 12 PM
    @Scheduled(cron = "0 0 0,12 * * *")
    public void scheduleProfileUpdates() {
        List<ProjectProfile> profiles = projectRepo.findAll();
        System.out.println("⏰ Starting scheduled Project profile refresh for " + profiles.size() + " users...");
        for (ProjectProfile profile : profiles) {
            if(profile.getGitHubProfile() != null) {
                projectProducer.sendFetchRequest(new ProjectFetchRequest(profile.getUsername(), "github", profile.getGitHubProfile().getUsername()));
            }
            if(profile.getHuggingFaceProfile() != null) {
                projectProducer.sendFetchRequest(new ProjectFetchRequest(profile.getUsername(), "huggingface", profile.getHuggingFaceProfile().getUsername()));
            }
            if(profile.getGitLabProfile() != null) {
                projectProducer.sendFetchRequest(new ProjectFetchRequest(profile.getUsername(), "gitlab", profile.getGitLabProfile().getUsername()));
            }
        }
        System.out.println("✅ Completed scheduled Project profile refresh cycle");
    }
}
