package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Fetch.CFFetch;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CFConsumer {

    private final CFFetch cfFetch;
    private final CpProfileRepo cpRepo;
    
    @RabbitListener(queues = RabbitMQConfig.cfQueue, concurrency = "3-5")
    public void handle(CPFetchRequest request) {
        System.out.println("📩 Codeforces consumer received: " + request.getHandle());
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        CpProfile profile = optionalProfile.get();
        try {
            CFProfile cfProfile = cfFetch.fetchProfile(request.getHandle());
            if(cfProfile==null) {
                throw new RuntimeException("Could not fetch profile");
            }
            profile.setCfProfile(cfProfile);
            cpRepo.save(profile);
        } catch(Exception e) {
            System.err.println("❌ Error fetching Codeforces profile for " + request.getHandle() + ": " + e.getMessage());
        }
        
    }
}
