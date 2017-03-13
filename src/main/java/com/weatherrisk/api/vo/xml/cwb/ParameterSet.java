package com.weatherrisk.api.vo.xml.cwb;

import java.util.List;

import lombok.Data;

@Data
public class ParameterSet {

	private String parameterSetName;
	
	private List<Parameter> parameter;
}
