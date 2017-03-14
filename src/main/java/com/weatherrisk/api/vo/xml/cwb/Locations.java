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
public class Locations {
	
	@XmlElement(name = "locationsName")
	private String locationsName;
	
	@XmlElement(name = "location")
	private List<Location> location;
}
