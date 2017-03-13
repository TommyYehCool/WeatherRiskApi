package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "parameter")
public class Parameter {
	private String parameterValue;
}
