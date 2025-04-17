package com.cryptoportfolio.models.responses;

/**
 * Builder class to build the result for the CreatePortfolioActivity using the request
 */
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePortfolioResponse {
    private String message;
}
