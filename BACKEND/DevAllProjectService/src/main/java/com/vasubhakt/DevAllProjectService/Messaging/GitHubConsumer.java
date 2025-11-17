package com.vasubhakt.DevAllProjectService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;
import com.vasubhakt.DevAllProjectService.Model.ProjectFetchRequest;
import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;
import com.vasubhakt.DevAllProjectService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitHubConsumer {
    
    private final ExternalAPIService externalApiService;
    private final ProjectRepo projectRepo;

    @RabbitListener(queues = RabbitMQConfig.gitHubQueue, concurrency = "3-5")
    public void handle(ProjectFetchRequest request) {
        System.out.println("📩 GitHub consumer received: " + request.getHandle());
        Optional<ProjectProfile> optionalProfile = projectRepo.findByUsername(request.getUsername());
        ProjectProfile profile = optionalProfile.get();
        try {
            GitHubProfile gitHubProfile = externalApiService.fetchGitHubProfile(request.getHandle());
            if(gitHubProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setGitHubProfile(gitHubProfile);
            projectRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching GitHub profile for " + request.getHandle() + ": " + e.getMessage());
        }
    }
}
