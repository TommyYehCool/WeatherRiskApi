package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "cwbopendata", namespace = "urn:cwb:gov:tw:cwbcommon:0.1")
@XmlAccessorType(XmlAccessType.FIELD)
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
