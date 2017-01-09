package com.weatherrisk.api.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsumeRepository extends MongoRepository<Consume, Long> {
	Consume findByLotteryNo(String lotteryNo); 
}
