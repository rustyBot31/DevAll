package com.vasubhakt.DevAllAuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "CP-SERVICE")
public interface CPClient {

    @PostMapping("/cp/create/{username}")
    public void createCPProfile(@PathVariable("username") String username);
    
}
