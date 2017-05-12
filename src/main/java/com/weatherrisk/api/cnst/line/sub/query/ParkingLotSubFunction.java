package com.weatherrisk.api.cnst.line.sub.query;

import com.weatherrisk.api.cnst.line.sub.LineSubFunction;

public enum ParkingLotSubFunction implements LineSubFunction {
	FIND_PARKING_LOT_BY_FUZZY_SEARCH("模糊搜尋"),
	FIND_PARING_LOT_BY_NAME("名稱搜尋");
	
	private String label;
	
	private ParkingLotSubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static ParkingLotSubFunction convertByName(String msg) {
		for (ParkingLotSubFunction e : ParkingLotSubFunction.values()) {
			if (e.toString().equals(msg)) {
				return e;
			}
		}
		return null;
	}
}
