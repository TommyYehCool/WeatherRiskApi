package com.weatherrisk.api.vo.xml.cwb;

import lombok.Data;

@Data
public class Location {
	private String locationName;
	private String stationId;
	private String geocode;
}
