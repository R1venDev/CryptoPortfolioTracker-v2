package com.cryptoportfolio.postgressDb.dao;

import com.cryptoportfolio.postgressDb.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PortfolioDao extends JpaRepository<Portfolio, Long> {

    // Find portfolios by username (for listing all portfolios of a user)
    List<Portfolio> findByUsername(String username);

    // Find a specific portfolio by username and portfolioId
    Optional<Portfolio> findByUsernameAndPortfolioId(String username, Long portfolioId);

    void deleteByPortfolioId(Long portfolioId);

    // Optionally, you can add more queries as needed, e.g., by username and portfolio name
}
