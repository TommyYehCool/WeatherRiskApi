package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "location")
public class Location {
	private String locationName;
	private String stationId;
	private String geocode;
}
