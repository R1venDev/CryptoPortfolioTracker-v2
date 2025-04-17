package com.cryptoportfolio.converter;

import com.cryptoportfolio.models.PortfolioModel;
import com.cryptoportfolio.models.TransactionModel;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import com.cryptoportfolio.postgressDb.models.Portfolio;
import com.cryptoportfolio.postgressDb.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelConverter {

    @Autowired
    private TransactionDao transactionDao;

    public PortfolioModel toPortfolioModel(Long portfolioId, Portfolio portfolio) {
        PortfolioModel portfolioModel = new PortfolioModel();
        portfolioModel.setId(portfolioId);
        portfolioModel.setPortfolioName(portfolio.getPortfolioName());
        portfolioModel.setUsername(portfolio.getUsername());
        portfolioModel.setAssetQuantityMap(portfolio.getAssetQuantityMap());
        return portfolioModel;
    }

    // Convert a list of portfolios to PortfolioModels
    public List<PortfolioModel> toPortfolioModelList(List<Portfolio> portfolios) {
        return portfolios.stream()
                .map(portfolio -> toPortfolioModel(portfolio.getPortfolioId(), portfolio))
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of {@link Transaction} to a list of {@link TransactionModel}.
     *
     * @param transactions List of transactions to convert
     * @return List of TransactionModel
     */
    public List<TransactionModel> toTransactionModelList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionModel)
                .collect(Collectors.toList());
    }

    /**
     * Converts a provided {@link Transaction} into a {@link TransactionModel} representation.
     * @param transaction the Transaction to convert
     * @return the converted TransactionModel
     */
    public TransactionModel toTransactionModel(Transaction transaction) {
        // Format the timestamp (LocalDateTime) to String
        String formattedTimestamp = formatTimestamp(transaction.getTimestamp());

        return TransactionModel.builder()
                .username(transaction.getUsername())
                .transactionDate(formattedTimestamp)  // Pass the formatted String
                .assetId(transaction.getAssetId())
                .assetQuantity(transaction.getQuantity())  // Using quantity directly
                .transactionValue(transaction.getPrice())  // Using price directly
                .transactionType(transaction.getTransactionType())  // Placeholder, you can update it based on actual logic
                .build();
    }

    /**
     * Converts LocalDateTime to String with a specific format.
     * @param timestamp the LocalDateTime to format
     * @return the formatted timestamp as a String
     */
    private String formatTimestamp(LocalDateTime timestamp) {
        // Define the format you want (e.g., "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp != null ? timestamp.format(formatter) : null;
    }

    /**
     * Fetch transactions for a specific user and assetId.
     * @param id the user for whom we need transactions
     * @param assetId the asset ID to filter transactions
     * @return the list of filtered TransactionModel objects
     */
    public List<TransactionModel> toTransactionModelList(Long id, String assetId) {
        List<Transaction> transactions = transactionDao.findByPortfolioIdAndAssetId(id, assetId);
        return transactions.stream()
                .map(this::toTransactionModel)
                .collect(Collectors.toList());
    }
}
