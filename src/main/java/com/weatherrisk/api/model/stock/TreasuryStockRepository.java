package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TreasuryStockRepository extends MongoRepository<TreasuryStock, String> {

}

