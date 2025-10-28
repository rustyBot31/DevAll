package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Model.LCProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;
import com.vasubhakt.DevAllCPService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LCConsumer {
    
    private final ExternalAPIService externalApiService;
    private final CpProfileRepo cpRepo;

    @RabbitListener(queues = RabbitMQConfig.lcQueue, concurrency = "3-5")
    public void handle(CPFetchRequest request) {
        System.out.println("📩 Leetcode consumer received: " + request.getHandle());
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        CpProfile profile = optionalProfile.get();

        LCProfile lcProfile = externalApiService.fetchLcProfile(request.getHandle());
        profile.setLcProfile(lcProfile);
        cpRepo.save(profile);
    }
}
