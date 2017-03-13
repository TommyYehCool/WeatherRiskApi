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
	@XmlElement(name = "identifier")
	private String identifier;
	
	@XmlElement(name = "sender")
	private String sender;
	
	@XmlElement(name = "sent")
	private String sent;
	
	@XmlElement(name = "status")
	private String status;
	
	@XmlElement(name = "msgType")
	private String msgType;
	
	@XmlElement(name = "scope")
	private String scope;
	
	@XmlElement(name = "dataid")
	private String dataid;
	
	@XmlElement(name = "source")
	private String source;
	
	@XmlElement(name = "dataset")
	private Dataset dataset;
}
