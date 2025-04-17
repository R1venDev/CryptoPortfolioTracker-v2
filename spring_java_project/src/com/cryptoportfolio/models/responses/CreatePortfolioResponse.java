package com.cryptoportfolio.models.responses;

/**
 * Builder class to build the result for the CreatePortfolioActivity using the request
 */
import com.cryptoportfolio.postgressDb.models.Portfolio;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePortfolioResponse {
        private String message;
        private Portfolio portfolio;
}
