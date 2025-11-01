package com.vasubhakt.DevAllCPService.Messaging;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;

import lombok.RequiredArgsConstructor;

@Async
@Component
@RequiredArgsConstructor
public class CPProfileScheduler {
    
    private final CPProducer cpProducer;
    private final CpProfileRepo cpRepo;

    // Runs every day at 12 AM and 12 PM
    @Scheduled(cron = "0 0 0,12 * * *")
    public void scheduleProfileUpdates() {
        List<CpProfile> profiles = cpRepo.findAll();
        System.out.println("⏰ Starting scheduled CP profile refresh for " + profiles.size() + " users...");
        for (CpProfile profile : profiles) {
            if(profile.getCfProfile() != null) {
                cpProducer.sendFetchRequest(new CPFetchRequest(profile.getUsername(), "codeforces", profile.getCfProfile().getHandle()));
            }
            if(profile.getLcProfile() != null) {
                cpProducer.sendFetchRequest(new CPFetchRequest(profile.getUsername(), "leetcode", profile.getLcProfile().getHandle()));
            }
            if(profile.getCcProfile() != null) {
                cpProducer.sendFetchRequest(new CPFetchRequest(profile.getUsername(), "codechef", profile.getCcProfile().getHandle()));
            }
            if(profile.getAcProfile() != null) {
                cpProducer.sendFetchRequest(new CPFetchRequest(profile.getUsername(), "atcoder", profile.getAcProfile().getHandle()));
            }
        }
        System.out.println("✅ Completed scheduled CP profile refresh cycle");
    }
}
