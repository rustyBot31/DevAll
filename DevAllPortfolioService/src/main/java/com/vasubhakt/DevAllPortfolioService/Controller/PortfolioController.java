package com.vasubhakt.DevAllPortfolioService.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vasubhakt.DevAllPortfolioService.Model.Portfolio;
import com.vasubhakt.DevAllPortfolioService.Service.PortfolioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/get")
    public ResponseEntity<?> getPortfolio(@RequestParam("username") String username) {
        try {
            Portfolio portfolio = portfolioService.getUserPortfolio(username);
            return ResponseEntity.ok(portfolio);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePortfolio(@RequestParam("username") String username, @RequestBody Portfolio updatedPortfolio) {
        try {
            updatedPortfolio.setUsername(username);
            Portfolio portfolio = portfolioService.updateUserPortfolio(updatedPortfolio);
            return ResponseEntity.ok(portfolio);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/create/{username}")
    public ResponseEntity<?> createPortfolio(@PathVariable String username) {
        try {
            portfolioService.createUserPortfolio(username);
            return ResponseEntity.ok("Portfolio created successfully");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deletePortfolio(@PathVariable("username") String username) {
        try {
            Portfolio portfolio = portfolioService.deleteUserPortfolio(username);
            return ResponseEntity.ok(portfolio);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
