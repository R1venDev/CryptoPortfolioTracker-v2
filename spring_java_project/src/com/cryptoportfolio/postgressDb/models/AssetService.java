package com.cryptoportfolio.postgressDb.models;



import com.cryptoportfolio.exceptions.AssetNotFoundException;
import com.cryptoportfolio.postgressDb.dao.AssetDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final AssetDao assetDao;

    @Autowired
    public AssetService(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    // Get an asset by ID
    public Asset getAsset(String assetId) {
        return assetDao.findById(assetId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found with ID: " + assetId));
    }

    // Get all assets
    public List<Asset> getAllAssets() {
        return assetDao.findAll();
    }

    // Save or update multiple assets
    public void saveAssets(List<Asset> assets) {
        assetDao.saveAll(assets);
    }
}
