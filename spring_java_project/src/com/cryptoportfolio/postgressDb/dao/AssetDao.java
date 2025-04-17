package com.cryptoportfolio.postgressDb.dao;

import com.cryptoportfolio.postgressDb.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetDao extends JpaRepository<Asset, String> {

    // Custom query method to find an asset by ID
    Optional<Asset> findById(String assetId);

    // Retrieve all assets
    List<Asset> findAll();
}