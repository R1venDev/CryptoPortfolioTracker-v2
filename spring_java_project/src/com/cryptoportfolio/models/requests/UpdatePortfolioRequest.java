package com.cryptoportfolio.models.requests;


import java.util.List;
import java.util.Map;

/**
 * The Builder class to create an Update request using the provided username and the asset quantity
 */
import com.cryptoportfolio.postgressDb.models.Transaction;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePortfolioRequest {
    private Long id;
    private String authToken;
    private Map<String, Double> assetQuantityMap;
    private List<Transaction> transactions;
}
