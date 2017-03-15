package com.weatherrisk.api.vo.json.tpeopendata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.vo.json.deserializer.ParkingLotAvailableDetailDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonDeserialize(using = ParkingLotAvailableDetailDeserializer.class)
@NoArgsConstructor
public class ParkingLotAvailableDetail {

	private String updateTime;
	
	private List<ParkingLotAvailable> parkingLotAvailables = new ArrayList<>();
	
	public void addParkingLotAvailable(ParkingLotAvailable parkingLotAvailable) {
		parkingLotAvailables.add(parkingLotAvailable);
	}
}
