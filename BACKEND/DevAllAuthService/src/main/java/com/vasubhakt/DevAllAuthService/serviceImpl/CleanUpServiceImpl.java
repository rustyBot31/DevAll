package com.vasubhakt.DevAllAuthService.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllAuthService.repo.UserRepository;
import com.vasubhakt.DevAllAuthService.service.CleanUpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CleanUpServiceImpl implements CleanUpService{

    private final UserRepository userRepo;

    @Override
    @Scheduled(cron = "0 0 0 * * *") //runs every day at midnight
    public void deleteUnverifiedUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        userRepo.deleteAllByEnabledFalseAndCreatedAtBefore(cutoff);
        System.out.println("🧹 Cleaned unverified users.");
    }

}
