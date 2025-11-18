package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Fetch.CCFetch;
import com.vasubhakt.DevAllCPService.Model.CCProfile;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CCConsumer {

    private final CCFetch ccFetch;
    private final CpProfileRepo cpRepo;

    @RabbitListener(queues = RabbitMQConfig.ccQueue, concurrency = "3-5")
    public void handle(CPFetchRequest request) {
        System.out.println("📩 Codechef consumer received: " + request.getHandle());
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        CpProfile profile = optionalProfile.get();
        try {
            CCProfile ccProfile = ccFetch.fetchProfile(request.getHandle());
            if(ccProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setCcProfile(ccProfile);
            cpRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching Codechef profile for " + request.getHandle() + ": " + e.getMessage());
        }
    }
}
