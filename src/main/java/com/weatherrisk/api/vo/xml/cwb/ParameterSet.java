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
public class ParameterSet {

	@XmlElement(name = "parameterSetName")
	private String parameterSetName;
	
	@XmlElement(name = "parameter")
	private List<Parameter> parameter;
}
