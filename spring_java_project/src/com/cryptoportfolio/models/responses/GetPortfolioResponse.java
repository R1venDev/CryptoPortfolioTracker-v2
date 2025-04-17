package com.cryptoportfolio.models.responses;

import java.util.ArrayList;
import java.util.List;
import com.cryptoportfolio.models.PortfolioModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetPortfolioResponse {

    // This field is used when there is a single portfolio.
    private PortfolioModel portfolio;

    // This field is used when there are multiple portfolios.
    private List<PortfolioModel> portfolios;

    private Boolean isEmpty;

    public GetPortfolioResponse() {
        this.portfolios = null;
        this.isEmpty = true;
        this.portfolio = null;
    }
    // Constructor to handle the case where multiple portfolios are returned.
    public GetPortfolioResponse(List<PortfolioModel> portfolios) {
        this.portfolios = portfolios;
        this.portfolio = null; // No single portfolio in this case
        this.isEmpty = false;
    }

    // Constructor to handle the case where only one portfolio is returned.
    public GetPortfolioResponse(PortfolioModel portfolio) {
        this.portfolio = portfolio;
        this.portfolios = null; // No list of portfolios in this case
        this.isEmpty = false;
    }
}
