package com.weatherrisk.api.vo.xml.cwb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "cwbopendata", namespace = "urn:cwb:gov:tw:cwbcommon:0.1")
@XmlAccessorType(XmlAccessType.FIELD)
public class CwbOpenData {
	@XmlElement
	private String identifier;
	
	@XmlElement
	private String sender;
	
	@XmlElement
	private String sent;
	
	@XmlElement
	private String status;
	
	@XmlElement
	private String msgType;
	
	@XmlElement
	private String scope;
	
	@XmlElement
	private String dataid;
	
	@XmlElement
	private String source;
	
	@XmlElement
	private Dataset dataset;
}
