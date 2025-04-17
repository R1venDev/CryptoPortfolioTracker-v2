package com.cryptoportfolio.postgressDb.models;

import com.cryptoportfolio.exceptions.MissingFieldException;
import com.cryptoportfolio.exceptions.TransactionsNotFoundException;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    // Fetch transactions by username and asset ID
    public List<Transaction> getTransactions(Long id, String assetFlag) {
        if (assetFlag == null) {
            throw new MissingFieldException("Asset Flag cannot be null");
        } else if (assetFlag.equalsIgnoreCase("ALL")) {
            List<Transaction> transactions = transactionDao.findByPortfolioId(id);
            if (transactions.isEmpty()) {
                throw new TransactionsNotFoundException("No transactions found for portfolioId: " + id);
            }
            return transactions;
        }

        List<Transaction> transactions = transactionDao.findByPortfolioIdAndAssetId(id, assetFlag);
        if (transactions.isEmpty()) {
            throw new TransactionsNotFoundException("No transactions found for portfolioid: " + id + " and asset ID: " + assetFlag);
        }
        return transactions;
    }

    // Save multiple transactions
    public void saveTransactions(List<Transaction> transactions) {
        try {
            transactionDao.saveAll(transactions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transactions", e);
        }
    }
}