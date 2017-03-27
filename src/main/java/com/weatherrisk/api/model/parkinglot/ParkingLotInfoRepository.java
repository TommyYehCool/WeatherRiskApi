package com.weatherrisk.api.model.parkinglot;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLotInfoRepository extends MongoRepository<ParkingLotInfo, String> {
	
	List<ParkingLotInfo> findByArea(String area);
	
	ParkingLotInfo findByName(String name);

	List<ParkingLotInfo> findByNameLike(String name);
}
