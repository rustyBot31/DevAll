package com.vasubhakt.DevAllAuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "PORTFOLIO-SERVICE")
public interface PortfolioClient {
    
    @PostMapping("/portfolio/create/{username}")
    public void createPortfolioProfile(@PathVariable("username") String username);

    @DeleteMapping("/portfolio/delete/{username}")
    public void deletePortfolioProfile(@PathVariable("username") String username);
}
