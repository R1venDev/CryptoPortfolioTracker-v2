package com.cryptoportfolio.activity;

import com.cryptoportfolio.exceptions.AssetNotAvailableException;
import com.cryptoportfolio.exceptions.UnableToSaveToDatabaseException;
import com.cryptoportfolio.models.requests.CreatePortfolioRequest;
import com.cryptoportfolio.models.responses.CreatePortfolioResponse;
import com.cryptoportfolio.postgressDb.dao.PortfolioDao;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import com.cryptoportfolio.postgressDb.models.Portfolio;
import com.cryptoportfolio.postgressDb.models.Transaction;
import com.cryptoportfolio.postgressDb.models.User;
import com.cryptoportfolio.postgressDb.models.UserService;
import com.cryptoportfolio.settings.Settings;
import com.cryptoportfolio.utils.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreatePortfolioActivity {

    private final PortfolioDao portfolioDao;
    private final UserService userService;
    private final TransactionDao transactionDao;

    @Autowired
    public CreatePortfolioActivity(PortfolioDao portfolioDao, UserService userService, TransactionDao transactionDao) {
        this.portfolioDao = portfolioDao;
        this.userService = userService;
        this.transactionDao = transactionDao;
    }

    /**
     * This method processes the request to create a portfolio, validates the assets,
     * saves transactions, and creates the portfolio.
     */
    public CreatePortfolioResponse execute(final CreatePortfolioRequest createPortfolioRequest) {
        // Validate the incoming request
        validateRequest(createPortfolioRequest);

        // Authenticate the user
        Auth.authenticateToken(createPortfolioRequest.getUsername(), createPortfolioRequest.getAuthToken());

        // Fetch and update user details
        User user = userService.getUser(createPortfolioRequest.getUsername());
        user.setIsNewUser(false);
        userService.updateUser(user);

        // Create and populate the portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setUsername(createPortfolioRequest.getUsername());
        portfolio.setAssetQuantityMap(filterNonZeroAssets(createPortfolioRequest.getAssetQuantityMap()));

        // Save transactions and portfolio

        Portfolio savedPortfolio = savePortfolio(portfolio);
        savedPortfolio.setPortfolioName(savedPortfolio.getUsername() + savedPortfolio.getPortfolioId());
        List<Transaction> transactionList = new ArrayList<>(createPortfolioRequest.getTransactions());
        transactionList.forEach( transaction -> transaction.setPortfolioId(savedPortfolio.getPortfolioId()));
        transactionDao.saveAll(transactionList);  // Use saveAll() instead of batchSaveTransactions()

        // Return the response with a success message
        return CreatePortfolioResponse.builder()
                .portfolio(savedPortfolio)
                .message("Portfolio created successfully")
                .build();
    }

    /**
     * Validates the request, ensuring that the assets are valid and available.
     */
    private void validateRequest(CreatePortfolioRequest request) {
        if (request.getTransactions() == null || request.getAssetQuantityMap() == null) {
            throw new AssetNotAvailableException("Resource not found: Asset(s) unavailable");
        }

        // Check if the requested assets are available in the system
        if (!Settings.AVAILABLE_ASSETS.containsAll(request.getAssetQuantityMap().keySet())) {
            throw new AssetNotAvailableException("Resource not found: Invalid asset(s)");
        }
    }

    /**
     * Filters out assets with zero quantity from the given map.
     */
    private Map<String, Double> filterNonZeroAssets(Map<String, Double> assetQuantityMap) {
        Map<String, Double> nonZeroAssets = new HashMap<>();
        assetQuantityMap.forEach((assetID, quantity) -> {
            if (quantity != 0) {
                nonZeroAssets.put(assetID, quantity);
            }
        });
        return nonZeroAssets;
    }

    /**
     * Attempts to save the portfolio to the database.
     * Throws an exception if saving fails.
     */
    private Portfolio savePortfolio(Portfolio portfolio) {
        try {
             return portfolioDao.save(portfolio);  // Assuming save() is the method on PortfolioDao
        } catch (Exception e) {
            throw new UnableToSaveToDatabaseException("Failed: Unable to save portfolio to the database", e);
        }
    }
}
