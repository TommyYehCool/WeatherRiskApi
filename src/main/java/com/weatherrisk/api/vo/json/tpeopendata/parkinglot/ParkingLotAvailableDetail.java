package com.weatherrisk.api.vo.json.tpeopendata.parkinglot;

import java.util.ArrayList;
import java.util.List;

import com.weatherrisk.api.model.parkinglot.ParkingLotAvailable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParkingLotAvailableDetail {

	private String updateTime;
	
	private List<ParkingLotAvailable> parkingLotAvailables = new ArrayList<>();
	
	public void addParkingLotAvailable(ParkingLotAvailable parkingLotAvailable) {
		parkingLotAvailables.add(parkingLotAvailable);
	}
}
