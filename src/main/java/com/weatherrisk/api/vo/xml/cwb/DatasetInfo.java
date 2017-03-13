package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "datasetinfo")
public class DatasetInfo {
	private String datasetDescription;
	private String datasetLanguage;
	private String issueTime;
}
