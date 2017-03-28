package com.weatherrisk.api.model.receiptreward;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceiptRewardRepository extends MongoRepository<ReceiptReward, String> {

	List<ReceiptReward> findBySection(String section);
}
