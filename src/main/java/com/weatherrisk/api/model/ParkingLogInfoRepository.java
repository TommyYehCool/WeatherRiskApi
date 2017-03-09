package com.weatherrisk.api.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLogInfoRepository extends MongoRepository<ParkingLotInfo, String> {

}
