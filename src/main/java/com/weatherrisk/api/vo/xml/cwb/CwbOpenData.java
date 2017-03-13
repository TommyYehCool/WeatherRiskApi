package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "cwbopendata", namespace = "urn:cwb:gov:tw:cwbcommon:0.1")
public class CwbOpenData {
	private String identifier;
	
	private String sender;
	
	private String sent;
	
	private String status;
	
	private String msgType;
	
	private String scope;
	
	private String dataid;
	
	private String source;
	
	private Dataset dataset;
}
