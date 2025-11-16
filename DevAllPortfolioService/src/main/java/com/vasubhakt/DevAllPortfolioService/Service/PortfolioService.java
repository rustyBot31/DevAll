package com.vasubhakt.DevAllPortfolioService.Service;

import com.vasubhakt.DevAllPortfolioService.Model.Portfolio;

public interface PortfolioService {
    
    Portfolio getUserPortfolio(String username);
    Portfolio updateUserPortfolio(Portfolio updatedPortfolio);
    void createUserPortfolio(String username);
    Portfolio deleteUserPortfolio(String username);
} 
