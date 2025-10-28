package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;
import com.vasubhakt.DevAllCPService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CFConsumer {

    private final ExternalAPIService externalApiService;
    private final CpProfileRepo cpRepo;

    @RabbitListener(queues = RabbitMQConfig.cfQueue, concurrency = "3-5")
    public void handle(CPFetchRequest request) {
        System.out.println("📩 Codeforces consumer received: " + request.getHandle());
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        CpProfile profile = optionalProfile.get();

        CFProfile cfProfile = externalApiService.fetchCfProfile(request.getHandle());
        profile.setCfProfile(cfProfile);
        cpRepo.save(profile);
    }
}
