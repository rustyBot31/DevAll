package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Fetch.LCFetch;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Model.LCProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LCConsumer {

    private final LCFetch lcFetch;
    private final CpProfileRepo cpRepo;

    @RabbitListener(queues = RabbitMQConfig.lcQueue, concurrency = "3-5")
    public void handle(CPFetchRequest request) {
        System.out.println("📩 LeetCode consumer received: " + request.getHandle());
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        CpProfile profile = optionalProfile.get();
        try {
            LCProfile lcProfile = lcFetch.fetchProfile(request.getHandle());
            if(lcProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setLcProfile(lcProfile);
            cpRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching LeetCode profile for " + request.getHandle() + ": " + e.getMessage());
        }
    }
}
