package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasetInfo {
	
	@XmlElement(name = "datasetDescription")
	private String datasetDescription;
	
	@XmlElement(name = "datasetLanguage")
	private String datasetLanguage;
	
	@XmlElement(name = "issueTime")
	private String issueTime;
}
