package com.weatherrisk.api.vo.xml.cwb;

import lombok.Data;

@Data
public class Dataset {
	private DatasetInfo datasetInfo;
	private Location location;
	private ParameterSet parameterSet;
}
