package com.weatherrisk.api.vo.xml.cwb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
	
	@XmlElement(name = "locationName")
	private String locationName;
	
	@XmlElement(name = "stationId")
	private String stationId;
	
	@XmlElement(name = "geocode")
	private String geocode;
	
	@XmlElement(name = "weatherElement")
	private List<WeatherElement> weatherElement;
}
