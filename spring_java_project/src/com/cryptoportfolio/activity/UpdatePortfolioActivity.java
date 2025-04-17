package com.cryptoportfolio.activity;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.cryptoportfolio.exceptions.AssetNotAvailableException;
import com.cryptoportfolio.exceptions.UnableToSaveToDatabaseException;
import com.cryptoportfolio.models.requests.UpdatePortfolioRequest;
import com.cryptoportfolio.models.responses.UpdatePortfolioResponse;
import com.cryptoportfolio.postgressDb.dao.PortfolioDao;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import com.cryptoportfolio.postgressDb.dao.UserDao;
import com.cryptoportfolio.postgressDb.models.Portfolio;
import com.cryptoportfolio.postgressDb.models.Transaction;
import com.cryptoportfolio.settings.Settings;
import com.cryptoportfolio.utils.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdatePortfolioActivity {

    private final Logger log = LogManager.getLogger();
    private final PortfolioDao portfolioDao;
    private final UserDao userDao;;
    private final TransactionDao transactionDao;

    @Autowired
    public UpdatePortfolioActivity(PortfolioDao portfolioDao, UserDao userDao, TransactionDao transactionDao) {
        this.portfolioDao = portfolioDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }

    @Transactional
    public UpdatePortfolioResponse execute(final UpdatePortfolioRequest updatePortfolioRequest) {
        // Validate the incoming request
        validateRequest(updatePortfolioRequest);

        // Fetch the existing portfolio by ID
        Portfolio existingPortfolio = portfolioDao.findById(updatePortfolioRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for ID: " + updatePortfolioRequest.getId()));

        // Authenticate the user
        Auth.authenticateToken(existingPortfolio.getUsername(), updatePortfolioRequest.getAuthToken());

        // Filter and update asset quantity map
        Map<String, Double> filteredAssetQuantityMap = filterNonZeroAssets(updatePortfolioRequest.getAssetQuantityMap());
        existingPortfolio.setAssetQuantityMap(filteredAssetQuantityMap);

        // Prepare and associate new transactions
        List<Transaction> transactionList = updatePortfolioRequest.getTransactions();
        for (Transaction transaction : transactionList) {
            transaction.setPortfolioId(existingPortfolio.getPortfolioId());
            transaction.setUsername(existingPortfolio.getUsername());
        }

        // Persist the updated portfolio and transactions
        saveTransactions(transactionList);
        portfolioDao.save(existingPortfolio);

        // Return a success response
        return UpdatePortfolioResponse.builder()
                .message("Portfolio updated successfully")
                .build();
    }


    private void validateRequest(UpdatePortfolioRequest request) {
        if (request.getTransactions() == null || request.getAssetQuantityMap() == null) {
            throw new AssetNotAvailableException("Resource not found: Asset(s) unavailable");
        }

        if (!Settings.AVAILABLE_ASSETS.containsAll(request.getAssetQuantityMap().keySet())) {
            throw new AssetNotAvailableException("Resource not found: Invalid asset(s)");
        }
    }

    private Map<String, Double> filterNonZeroAssets(Map<String, Double> assetQuantityMap) {
        Map<String, Double> nonZeroAssets = new HashMap<>();
        assetQuantityMap.forEach((assetID, quantity) -> {
            if (quantity != 0) {
                nonZeroAssets.put(assetID, quantity);
            }
        });
        return nonZeroAssets;
    }

    private void saveTransactions(List<Transaction> transactionList) {
        try {
            // Use batch insert for saving multiple transactions at once to improve performance
            if (!transactionList.isEmpty()) {
                transactionDao.saveAll(transactionList);
                log.info("Transactions saved successfully.");
            }
        } catch (Exception e) {
            log.error("Error saving transactions", e);
            throw new UnableToSaveToDatabaseException("Failed: Unable to save transactions to the database", e);
        }
    }

    private void savePortfolio(Portfolio portfolio) {
        try {
            // Save or update the portfolio in the database
            portfolioDao.save(portfolio); // This will either create a new or update the existing portfolio
            log.info("Portfolio saved successfully.");
        } catch (Exception e) {
            log.error("Error saving portfolio", e);
            throw new UnableToSaveToDatabaseException("Failed: Unable to save portfolio to the database", e);
        }
    }
}
