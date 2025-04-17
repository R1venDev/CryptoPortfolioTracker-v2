package com.cryptoportfolio.postgressDb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "portfolios") // Specify the table name
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generate a unique ID for each portfolio
    private Long portfolioId;  // Unique ID for each portfolio

    @Column(name = "username")
    private String username;  // User to whom this portfolio belongs

    @Column(name = "portfolioName")
    private String portfolioName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false, nullable = false)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "portfolio_assets", joinColumns = @JoinColumn(name = "portfolio_id"))
    @MapKeyColumn(name = "asset_id")
    @Column(name = "quantity")
    private Map<String, Double> assetQuantityMap;

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, Double> getAssetQuantityMap() {
        return assetQuantityMap;
    }

    public void setAssetQuantityMap(Map<String, Double> assetQuantityMap) {
        this.assetQuantityMap = assetQuantityMap;
    }
}
