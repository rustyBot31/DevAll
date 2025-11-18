package com.vasubhakt.DevAllProjectService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllProjectService.Fetch.GitLabFetch;
import com.vasubhakt.DevAllProjectService.Model.GitLabProfile;
import com.vasubhakt.DevAllProjectService.Model.ProjectFetchRequest;
import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitLabConsumer {
    
    private final GitLabFetch gitLabFetch;
    private final ProjectRepo projectRepo;

    @RabbitListener(queues = RabbitMQConfig.gitLabQueue, concurrency = "3-5")
    public void handle(ProjectFetchRequest request) {
        System.out.println("📩 GitLab consumer received: " + request.getHandle());
        Optional<ProjectProfile> optionalProfile = projectRepo.findByUsername(request.getUsername());
        ProjectProfile profile = optionalProfile.get();
        try {
            GitLabProfile gitLabProfile = gitLabFetch.fetchProfile(request.getHandle());
            if(gitLabProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setGitLabProfile(gitLabProfile);
            projectRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching GitLab profile for " + request.getHandle() + ": " + e.getMessage());
        }
    }
}
