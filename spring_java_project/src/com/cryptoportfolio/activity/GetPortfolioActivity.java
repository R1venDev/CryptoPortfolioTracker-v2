package com.cryptoportfolio.activity;

import com.cryptoportfolio.converter.ModelConverter;
import com.cryptoportfolio.exceptions.PortfolioNotFoundException;
import com.cryptoportfolio.models.PortfolioModel;
import com.cryptoportfolio.models.requests.GetPortfolioRequest;
import com.cryptoportfolio.models.responses.GetPortfolioResponse;
import com.cryptoportfolio.postgressDb.dao.PortfolioDao;
import com.cryptoportfolio.postgressDb.models.Portfolio;
import com.cryptoportfolio.utils.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the GetPortfolioActivity for the CryptoPortfolioTracker's GetPortfolio API.
 * This API allows the customer to retrieve their saved portfolios and any related transactions.
 */
@Service
public class GetPortfolioActivity {

    private final Logger log = LogManager.getLogger();
    private final PortfolioDao portfolioDao;
    private final ModelConverter modelConverter;

    /**
     * Instantiates a new GetPortfolioActivity object.
     * Spring will automatically inject the dependencies.
     *
     * @param portfolioDao PortfolioDao dependency
     * @param modelConverter ModelConverter dependency
     */
    @Autowired
    public GetPortfolioActivity(PortfolioDao portfolioDao, ModelConverter modelConverter) {
        this.portfolioDao = portfolioDao;
        this.modelConverter = modelConverter;
    }

    /**
     * This method handles the incoming request by retrieving the portfolio(s) from the database for the provided username.
     * If no portfolio is found or an invalid portfolioId is provided, it throws a PortfolioNotFoundException.
     *
     * @param getPortfolioRequest request object containing the username, portfolioId, and auth token
     * @return GetPortfolioResponse containing the portfolio model(s)
     */
    public GetPortfolioResponse execute(GetPortfolioRequest getPortfolioRequest) {

        String username = getPortfolioRequest.getUsername();
        Long portfolioId = getPortfolioRequest.getPortfolioId();
        String authToken = getPortfolioRequest.getAuthToken();

        // Authenticate the user using the token
        Auth.authenticateToken(username, authToken);

        // Retrieve portfolios for the given username
        List<Portfolio> portfolios = portfolioDao.findByUsername(username);

        if (portfolios.isEmpty()) {
            throw new PortfolioNotFoundException("No portfolios found for username: " + username);
        }

        // If portfolioId is provided, return the specific portfolio
        if (portfolioId != null) {
            Optional<Portfolio> portfolioOpt = portfolioDao.findByUsernameAndPortfolioId(username, portfolioId);
            if (portfolioOpt.isEmpty()) {
                throw new PortfolioNotFoundException("Could not find Portfolio with ID: " + portfolioId);
            }
            Portfolio portfolio = portfolioOpt.get();
            PortfolioModel portfolioModel = modelConverter.toPortfolioModel(portfolioId, portfolio);
            return GetPortfolioResponse.builder()
                    .portfolio(portfolioModel) // Single portfolio response
                    .build();
        }

        // Return all portfolios if no portfolioId is provided
        List<PortfolioModel> portfolioModels = modelConverter.toPortfolioModelList(portfolios);
        return GetPortfolioResponse.builder()
                .portfolios(portfolioModels) // Multiple portfolios response
                .build();
    }

}
