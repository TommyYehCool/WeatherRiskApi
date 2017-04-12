package com.weatherrisk.api.model.stock;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TreasuryStockRepository extends MongoRepository<TreasuryStock, String> {
	
	List<TreasuryStock> findByUserId(String userId);
}

