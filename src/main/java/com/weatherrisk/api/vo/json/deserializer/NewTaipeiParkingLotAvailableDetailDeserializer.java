package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.parkinglot.ParkingLotAvailable;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotAvailableDetail;

public class NewTaipeiParkingLotAvailableDetailDeserializer extends JsonDeserializer<ParkingLotAvailableDetail> {

	@Override
	public ParkingLotAvailableDetail deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ParkingLotAvailableDetail result = new ParkingLotAvailableDetail();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    Iterator<JsonNode> parks = rootNode.elements();
	    while (parks.hasNext()) {
	    	JsonNode park = parks.next();
	    	
	    	String id = park.get("ID").asText();
	    	int availableCar = park.get("AVAILABLECAR").asInt();
	    	int availableMotor = park.get("AVAILABLEMOTOR") != null ? park.get("AVAILABLEMOTOR").asInt() : -9;
	    	
	    	ParkingLotAvailable parkingLotAvailable 
	    		= new ParkingLotAvailable(id, availableCar, availableMotor);
	    	
	    	result.addParkingLotAvailable(parkingLotAvailable);
	    }

		return result;
	}

}
