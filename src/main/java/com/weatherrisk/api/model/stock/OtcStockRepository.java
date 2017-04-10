package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtcStockRepository extends MongoRepository<OtcStock, String> {

	OtcStock findById(String id);
	
	OtcStock findByName(String name);
}
