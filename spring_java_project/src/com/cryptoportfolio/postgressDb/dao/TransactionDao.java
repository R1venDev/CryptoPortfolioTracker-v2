package com.cryptoportfolio.postgressDb.dao;

import com.cryptoportfolio.postgressDb.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {

    // Custom query method to fetch transactions by username and asset ID
    List<Transaction> findByPortfolioIdAndAssetId(Long portfolioId, String assetId);

    // Custom query method to fetch all transactions for a username
    List<Transaction> findByPortfolioId(Long portfolioId);

    // Custom delete method to delete transactions by portfolioId
    void deleteByPortfolioId(Long portfolioId);
}