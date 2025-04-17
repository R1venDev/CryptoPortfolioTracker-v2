package com.cryptoportfolio.models;

import java.util.Objects;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionModel {
    private String username;
    private String transactionDate;
    private String assetId;
    private double assetQuantity;
    private double transactionValue;
    private String transactionType;
}
