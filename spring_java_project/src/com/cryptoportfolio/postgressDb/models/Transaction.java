package com.cryptoportfolio.postgressDb.models;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions") // Specify the table name
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private Long portfolioId;

    @Column
    private String assetId;

    @Column
    private double quantity;

    @Column
    private double price;

    @Column
    private String  transactionType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    // Getters and Setters

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Set the timestamp to the current time before persisting
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
