package com.cryptoportfolio.models;


import java.util.Map;
import lombok.*;
import java.util.List;


/**
 * Portfolio model for multiple portfolios per user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioModel {
    private Long id;  // Unique portfolio ID
    private String username;  // Associated user
    private String portfolioName;  // Name for each portfolio
    private Map<String, Double> assetQuantityMap;  // Asset and quantity mapping
    private List<TransactionModel> transactions;  // List of transactions
}
