package com.cryptoportfolio.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPortfolioRequest {
    private String username;   // Username to identify the user
    private Long portfolioId;  // Portfolio ID to fetch a specific portfolio
    private String authToken;  // Authentication token for validation
}
