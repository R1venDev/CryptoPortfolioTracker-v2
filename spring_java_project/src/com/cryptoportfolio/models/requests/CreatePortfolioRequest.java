package com.cryptoportfolio.models.requests;


import java.util.List;
import java.util.Map;

/**
 * The Builder class to create a portfolio request using the provided username and the assetId, quantity mapping
 */
import com.cryptoportfolio.postgressDb.models.Transaction;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePortfolioRequest {
    private String username;
    private String authToken;
    private Map<String, Double> assetQuantityMap;
    private List<Transaction> transactions;
}
