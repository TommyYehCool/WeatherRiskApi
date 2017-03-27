package com.weatherrisk.api.model.parkinglot;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_lot_availables")
public class ParkingLotAvailable {
	@Id
	private String id;
	private int availableCar;
	private int availableMotor;
}
