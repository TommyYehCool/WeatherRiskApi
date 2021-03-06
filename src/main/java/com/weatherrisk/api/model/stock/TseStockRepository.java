package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TseStockRepository extends MongoRepository<TseStock, String> {

	TseStock findById(String id);
	
	TseStock findByName(String name);
}
