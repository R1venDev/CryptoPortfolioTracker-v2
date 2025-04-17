package com.cryptoportfolio.models.requests;


import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The Builder class to create a portfolio request using the provided username and the assetId, quantity mapping
 */
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetTransactionsRequest {
    private Long portfolioId;
    private String authToken;
    private String assetFlag;
}
