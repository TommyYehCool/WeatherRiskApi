package com.weatherrisk.api.vo.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.vo.json.deserializer.ParkingLotInfoDetailDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonDeserialize(using = ParkingLotInfoDetailDeserializer.class)
@NoArgsConstructor
public class ParkingLotInfoDetail {
	
	private String updateTime;
	
	private List<ParkingLotInfo> parkingLotInfos = new ArrayList<>();
	
	public void addParkingLotInfo(ParkingLotInfo parkingLotInfo) {
		parkingLotInfos.add(parkingLotInfo);
	}
}
