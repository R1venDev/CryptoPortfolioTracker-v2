package com.cryptoportfolio.postgressDb.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "assets") // Specify the table name
public class Asset {

    @Id
    private String assetId;

    private String name;
    private double value;

    // Getters and Setters

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
