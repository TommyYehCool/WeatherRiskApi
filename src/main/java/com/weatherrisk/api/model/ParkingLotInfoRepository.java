package com.weatherrisk.api.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLotInfoRepository extends MongoRepository<ParkingLotInfo, String> {
	
	ParkingLotInfo findById(String id);
	
	List<ParkingLotInfo> findByArea(String area);
	
	List<ParkingLotInfo> findByName(String name);

}
