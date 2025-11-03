package com.vasubhakt.DevAllAuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "PROJECT-SERVICE")
public interface ProjectClient {

    @PostMapping("/project/create/{username}")
    public void createProjectProfile(@PathVariable("username") String username);
} 
