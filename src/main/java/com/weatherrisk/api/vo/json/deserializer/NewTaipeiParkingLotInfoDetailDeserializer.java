package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotInfoDetail;

public class NewTaipeiParkingLotInfoDetailDeserializer extends JsonDeserializer<ParkingLotInfoDetail> {

	@Override
	public ParkingLotInfoDetail deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		ParkingLotInfoDetail result = new ParkingLotInfoDetail();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
		
	    Iterator<JsonNode> parks = rootNode.elements();
	    while (parks.hasNext()) {
	    	JsonNode park = parks.next();
	    	String id = park.get("ID").asText();
	    	String area = park.get("AREA").asText();
	    	String name = park.get("NAME").asText();
	    	String summary = park.get("SUMMARY").asText();
	    	String address = park.get("ADDRESS").asText();
	    	String tel = park.get("TEL").asText();
	    	String payex = park.get("PAYEX").asText();
	    	String serviceTime = park.get("SERVICETIME").asText();
	    	int totalCar = park.get("TOTALCAR").asInt();
	    	int totalMotor = park.get("TOTALMOTOR").asInt();
	    	int totalBike = park.get("TOTALBIKE").asInt();
	    	
	    	ParkingLotInfo parkingLotInfo 
	    		= new ParkingLotInfo(id, area, name, summary, address, tel, payex, serviceTime, totalCar, totalMotor, totalBike);
	    	
	    	result.addParkingLotInfo(parkingLotInfo);
	    }
	    
		return result;
	}

}
