package com.vasubhakt.DevAllProjectService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceProfile;
import com.vasubhakt.DevAllProjectService.Model.ProjectFetchRequest;
import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;
import com.vasubhakt.DevAllProjectService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HuggingFaceConsumer {
    
    private final ExternalAPIService externalAPIService;
    private final ProjectRepo projectRepo;

    @RabbitListener(queues = RabbitMQConfig.huggingFaceQueue, concurrency = "3-5")
    public void handle(ProjectFetchRequest request) {
        System.out.println("📩 HuggingFace consumer received: " + request.getHandle());
        Optional<ProjectProfile> optionalProfile = projectRepo.findByUsername(request.getUsername());
        ProjectProfile profile = optionalProfile.get();
        try {
            HuggingFaceProfile huggingFaceProfile = externalAPIService.fetchHuggingFaceProfile(request.getHandle());
            if(huggingFaceProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setHuggingFaceProfile(huggingFaceProfile);
            projectRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching HuggingFace profile for " + request.getHandle() + ": " + e.getMessage());
        }
    }
}
