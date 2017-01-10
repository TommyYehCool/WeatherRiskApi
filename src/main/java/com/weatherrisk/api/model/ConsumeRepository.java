package com.weatherrisk.api.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsumeRepository extends MongoRepository<Consume, Long> {
	Consume findByLotteryNo(String lotteryNo);
	
	List<Consume> findByProdNameStartingWith(String prodName);
}
