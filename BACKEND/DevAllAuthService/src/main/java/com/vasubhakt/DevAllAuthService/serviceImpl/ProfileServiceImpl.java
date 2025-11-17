package com.vasubhakt.DevAllAuthService.serviceImpl;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllAuthService.client.CPClient;
import com.vasubhakt.DevAllAuthService.client.PortfolioClient;
import com.vasubhakt.DevAllAuthService.client.ProjectClient;
import com.vasubhakt.DevAllAuthService.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    
    private final CPClient cpClient;
    private final ProjectClient projectClient;
    private final PortfolioClient portfolioClient;

    @Async
    @Override
    public CompletableFuture<Void> createCP(String username) {
        cpClient.createCPProfile(username);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> createProject(String username) {
        projectClient.createProjectProfile(username);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> createPortfolio(String username) {
        portfolioClient.createPortfolioProfile(username);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteCP(String username) {
        cpClient.deleteCPProfile(username);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteProject(String username) {
        projectClient.deleteProjectProfile(username);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Void> deletePortfolio(String username) {
        portfolioClient.deletePortfolioProfile(username);
        return CompletableFuture.completedFuture(null);
    }
}
