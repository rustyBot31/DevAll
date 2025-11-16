package com.vasubhakt.DevAllPortfolioService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllPortfolioService.Model.Portfolio;
import com.vasubhakt.DevAllPortfolioService.Repo.PortfolioRepo;
import com.vasubhakt.DevAllPortfolioService.Service.PortfolioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepo portfolioRepo;
    
    @Override
    public Portfolio getUserPortfolio(String username) {
        return portfolioRepo.findByUsername(username)
               .orElseThrow(() -> new RuntimeException("User Portfolio not found"));
    }

    @Override
    public Portfolio updateUserPortfolio(Portfolio updatedPortfolio) {
        Portfolio existingPortfolio = portfolioRepo.findByUsername(updatedPortfolio.getUsername())
                .orElseThrow(() -> new RuntimeException("User Portfolio not found"));
        if(updatedPortfolio.getName() != null) {
            existingPortfolio.setName(updatedPortfolio.getName());
        }
        if(updatedPortfolio.getSummary() != null) {
            existingPortfolio.setSummary(updatedPortfolio.getSummary());
        }
        if(updatedPortfolio.getLanguages() != null) {
            existingPortfolio.setLanguages(updatedPortfolio.getLanguages());
        }
        if(updatedPortfolio.getFrameworks() != null) {
            existingPortfolio.setFrameworks(updatedPortfolio.getFrameworks());
        }
        if(updatedPortfolio.getTools() != null) {
            existingPortfolio.setTools(updatedPortfolio.getTools());
        }
        if(updatedPortfolio.getDatabases() != null) {
            existingPortfolio.setDatabases(updatedPortfolio.getDatabases());
        }
        if(updatedPortfolio.getOperatingSystems() != null) {
            existingPortfolio.setOperatingSystems(updatedPortfolio.getOperatingSystems());
        }
        if(updatedPortfolio.getCsFundamentals() != null) {
            existingPortfolio.setCsFundamentals(updatedPortfolio.getCsFundamentals());
        }
        if(updatedPortfolio.getProjects() != null) {
            existingPortfolio.setProjects(updatedPortfolio.getProjects());
        }
        if(updatedPortfolio.getResumeLink() != null) {
            existingPortfolio.setResumeLink(updatedPortfolio.getResumeLink());
        }
        if(updatedPortfolio.getLinkedInLink() != null) {
            existingPortfolio.setLinkedInLink(updatedPortfolio.getLinkedInLink());
        }
        if(updatedPortfolio.getStackOverflowLink() != null) {
            existingPortfolio.setStackOverflowLink(updatedPortfolio.getStackOverflowLink());
        }
        portfolioRepo.save(existingPortfolio);
        return existingPortfolio;        
    }

    @Override
    public void createUserPortfolio(String username) {
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setUsername(username);
        portfolioRepo.save(newPortfolio);
    }

    @Override
    public Portfolio deleteUserPortfolio(String username) {
        Portfolio existingPortfolio = portfolioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Portfolio not found"));
        portfolioRepo.delete(existingPortfolio);
        return existingPortfolio;
    }
}
