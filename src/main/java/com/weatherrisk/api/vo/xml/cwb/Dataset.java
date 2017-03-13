package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Dataset {

	@XmlElement(name = "datasetInfo")
	private DatasetInfo datasetInfo;
	
	@XmlElement(name = "location")
	private Location location;
	
	@XmlElement(name = "parameterSet")
	private ParameterSet parameterSet;
}
