package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "dataset")
public class Dataset {
	private DatasetInfo datasetInfo;
	private Location location;
	private ParameterSet parameterSet;
}
