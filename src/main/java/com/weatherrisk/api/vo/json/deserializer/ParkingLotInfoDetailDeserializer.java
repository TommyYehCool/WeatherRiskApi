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
import com.weatherrisk.api.vo.json.tpeopendata.ParkingLotInfoDetail;

public class ParkingLotInfoDetailDeserializer extends JsonDeserializer<ParkingLotInfoDetail> {

	@Override
	public ParkingLotInfoDetail deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		ParkingLotInfoDetail result = new ParkingLotInfoDetail();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
		
	    JsonNode dataNode = rootNode.get("data");
	    
	    String updateTime = dataNode.get("UPDATETIME").asText();
	    result.setUpdateTime(updateTime);
	    
	    Iterator<JsonNode> parks = dataNode.path("park").elements();
	    while (parks.hasNext()) {
	    	JsonNode park = parks.next();
	    	String id = park.get("id").asText();
	    	String area = park.get("area").asText();
	    	String name = park.get("name").asText();
	    	String summary = park.get("summary").asText();
	    	String address = park.get("address").asText();
	    	String tel = park.get("tel").asText();
	    	String payex = park.get("payex").asText();
	    	String serviceTime = park.get("servicetime").asText();
	    	int totalCar = park.get("totalcar").asInt();
	    	int totalMotor = park.get("totalmotor").asInt();
	    	int totalBike = park.get("totalbike").asInt();
	    	
	    	ParkingLotInfo parkingLotInfo 
	    		= new ParkingLotInfo(id, area, name, summary, address, tel, payex, serviceTime, totalCar, totalMotor, totalBike);
	    	
	    	result.addParkingLotInfo(parkingLotInfo);
	    }
	    
		return result;
	}

}
