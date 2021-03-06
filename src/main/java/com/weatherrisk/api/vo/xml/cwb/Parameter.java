package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
	
	@XmlElement(name = "parameterName")
	private String parameterName;
	
	@XmlElement(name = "parameterValue")
	private String parameterValue;
	
	@XmlElement(name = "parameterUnit")
	private String parameterUnit;
}
