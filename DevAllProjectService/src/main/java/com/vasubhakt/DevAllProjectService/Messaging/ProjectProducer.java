package com.vasubhakt.DevAllProjectService.Messaging;

import java.util.Optional;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllProjectService.Model.ProjectFetchRequest;
import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Repo.ProjectRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectProducer {
    
    private final AmqpTemplate amqpTemplate;
    private final ProjectRepo projectRepo;

    public void sendFetchRequest(ProjectFetchRequest request) {
        Optional<ProjectProfile> optionalProfile = projectRepo.findByUsername(request.getUsername());
        if(optionalProfile.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }
        String routingKey = switch(request.getPlatform().toLowerCase()) {
            case "github" -> RabbitMQConfig.gitHubKey;
            default -> throw new IllegalArgumentException("Unsupported platform: " + request.getPlatform());
        };

        amqpTemplate.convertAndSend(RabbitMQConfig.exchange, routingKey, request);
        System.out.println("📤 Sent fetch request for " + request.getPlatform() + " handle: " + request.getHandle());
    }

}
