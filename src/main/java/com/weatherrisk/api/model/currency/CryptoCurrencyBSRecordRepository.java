package com.weatherrisk.api.model.currency;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CryptoCurrencyBSRecordRepository extends MongoRepository<CryptoCurrencyBSRecord, String> {
	
	List<CryptoCurrencyBSRecord> findByUserId(String userId);

}
