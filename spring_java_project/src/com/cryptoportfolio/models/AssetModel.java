package com.cryptoportfolio.models;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetModel {

    private String assetId;
    private int rankByMarketCap;
    private double marketCap;
    private String assetImage;
    private String assetName;
    private String assetSymbol;
    private double totalSupply;
    private double usdValue;
    private double priceChangePercentage24h;
}
