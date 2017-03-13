package com.weatherrisk.api.vo.xml.cwb;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "parameterSet")
public class ParameterSet {

	private String parameterSetName;
	
	private List<Parameter> parameter;
}
