package com.cryptoportfolio.postgressDb.models;

import com.cryptoportfolio.exceptions.PortfolioNotFoundException;
import com.cryptoportfolio.exceptions.UnableToSaveToDatabaseException;
import com.cryptoportfolio.postgressDb.dao.PortfolioDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioDao portfolioDao;

    @Autowired
    public PortfolioService(PortfolioDao portfolioDao) {
        this.portfolioDao = portfolioDao;
    }

    // Get all portfolios for a given username
    public List<Portfolio> getPortfolios(String username) {
        List<Portfolio> portfolios = portfolioDao.findByUsername(username);
        if (portfolios.isEmpty()) {
            throw new PortfolioNotFoundException("No portfolios found for username: " + username);
        }
        return portfolios;
    }

    // Get a specific portfolio by username and portfolio ID
    public Portfolio getPortfolio(String username, Long portfolioId) {
        Optional<Portfolio> portfolioOpt = portfolioDao.findByUsernameAndPortfolioId(username, portfolioId);
        return portfolioOpt.orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found for username: " + username + " and portfolio ID: " + portfolioId));
    }

    // Add a new portfolio
    public Portfolio addPortfolio(String username, Portfolio portfolio) {
        // Optionally, set the username in the portfolio if not already set
        portfolio.setUsername(username);

        try {
            return portfolioDao.save(portfolio);
        } catch (Exception e) {
            throw new UnableToSaveToDatabaseException("Failed to save portfolio to the database", e);
        }
    }

    // Update an existing portfolio
    public Portfolio updatePortfolio(String username, Long portfolioId, Portfolio updatedPortfolio) {
        Portfolio existingPortfolio = getPortfolio(username, portfolioId);
        existingPortfolio.setAssetQuantityMap(updatedPortfolio.getAssetQuantityMap());  // Example of updating assets
        // Set additional fields if needed based on your update logic

        try {
            return portfolioDao.save(existingPortfolio);
        } catch (Exception e) {
            throw new UnableToSaveToDatabaseException("Failed to update portfolio", e);
        }
    }

    // Optionally, delete a portfolio (if needed)
    public void deletePortfolio(String username, Long portfolioId) {
        Portfolio portfolio = getPortfolio(username, portfolioId);
        try {
            portfolioDao.delete(portfolio);
        } catch (Exception e) {
            throw new UnableToSaveToDatabaseException("Failed to delete portfolio", e);
        }
    }
}
