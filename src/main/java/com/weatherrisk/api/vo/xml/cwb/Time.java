package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Time {
	
	@XmlElement(name = "startTime")
	private String startTime;
	
	@XmlElement(name = "endTime")
	private String endTime;
	
	@XmlElement(name = "parameter")
	private Parameter parameter;
}
