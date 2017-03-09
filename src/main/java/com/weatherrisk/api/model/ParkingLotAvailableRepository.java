package com.weatherrisk.api.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLotAvailableRepository extends MongoRepository<ParkingLotAvailable, String> {

	ParkingLotAvailable findById(String id);

	List<ParkingLotAvailable> findByIdIn(List<String> ids);
}
