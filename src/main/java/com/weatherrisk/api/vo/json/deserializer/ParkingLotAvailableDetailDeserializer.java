package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.vo.json.tpeopendata.ParkingLotAvailableDetail;

public class ParkingLotAvailableDetailDeserializer extends JsonDeserializer<ParkingLotAvailableDetail> {

	@Override
	public ParkingLotAvailableDetail deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ParkingLotAvailableDetail result = new ParkingLotAvailableDetail();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    JsonNode dataNode = rootNode.get("data");
	    
	    String updateTime = dataNode.get("UPDATETIME").asText();
	    result.setUpdateTime(updateTime);
	    
	    Iterator<JsonNode> parks = dataNode.path("park").elements();
	    while (parks.hasNext()) {
	    	JsonNode park = parks.next();
	    	
	    	String id = park.get("id").asText();
	    	int availableCar = park.get("availablecar").asInt();
	    	int availableMotor = park.get("availablemotor").asInt();
	    	
	    	ParkingLotAvailable parkingLotAvailable 
	    		= new ParkingLotAvailable(id, availableCar, availableMotor);
	    	
	    	result.addParkingLotAvailable(parkingLotAvailable);
	    }

		return result;
	}

}
