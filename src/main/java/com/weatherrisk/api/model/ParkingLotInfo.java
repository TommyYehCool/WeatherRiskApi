package com.weatherrisk.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_lot_infos")
public class ParkingLotInfo {
	@Id
	private String id;
	private String area;
	private String name;
	private String summary;
	private String address;
	private String tel;
	private String payex;
	private String serviceTime;
	private int totalCar;
	private int totalMotor;
	private int totalBike;
}
