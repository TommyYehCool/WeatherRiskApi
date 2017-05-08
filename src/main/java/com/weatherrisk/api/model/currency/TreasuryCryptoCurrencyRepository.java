package com.weatherrisk.api.model.currency;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TreasuryCryptoCurrencyRepository extends MongoRepository<TreasuryCryptoCurrency, String> {
	
	List<TreasuryCryptoCurrency> findByUserId(String userId);
}

