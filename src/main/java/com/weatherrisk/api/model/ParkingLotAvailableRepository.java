package com.weatherrisk.api.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLotAvailableRepository extends MongoRepository<ParkingLotAvailable, String> {

	ParkingLotAvailable findById(String id);

}
