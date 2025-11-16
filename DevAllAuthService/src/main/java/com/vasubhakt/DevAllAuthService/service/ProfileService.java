package com.vasubhakt.DevAllAuthService.service;

import java.util.concurrent.CompletableFuture;

public interface ProfileService {
    
    public CompletableFuture<Void> createCP(String username);
    public CompletableFuture<Void> createProject(String username);
    public CompletableFuture<Void> createPortfolio(String username);
    public CompletableFuture<Void> deleteCP(String username);
    public CompletableFuture<Void> deleteProject(String username);
    public CompletableFuture<Void> deletePortfolio(String username);
}
