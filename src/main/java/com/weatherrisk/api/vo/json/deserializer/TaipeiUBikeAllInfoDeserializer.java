package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeAllInfo;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

public class TaipeiUBikeAllInfoDeserializer extends JsonDeserializer<UBikeAllInfo> {

	@Override
	public UBikeAllInfo deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		UBikeAllInfo result = new UBikeAllInfo();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    JsonNode retValNodes = rootNode.get("retVal");
	    
	    Iterator<JsonNode> itRetValNodes = retValNodes.elements();
	    while (itRetValNodes.hasNext()) {
	    	UBikeInfo ubikeInfo = new UBikeInfo();
	    	
	    	JsonNode retValNode = itRetValNodes.next();
	    	
	    	ubikeInfo.setSno(retValNode.get("sno").asText());
	    	ubikeInfo.setSna(retValNode.get("sna").asText());
	    	ubikeInfo.setTot(retValNode.get("tot").asInt());
	    	ubikeInfo.setSbi(retValNode.get("sbi").asInt());
	    	ubikeInfo.setSarea(retValNode.get("sarea").asText());
	    	ubikeInfo.setMday(retValNode.get("mday").asText());
	    	ubikeInfo.setLat(retValNode.get("lat").asDouble());
	    	ubikeInfo.setLng(retValNode.get("lng").asDouble());
	    	ubikeInfo.setAr(retValNode.get("ar").asText());
	    	ubikeInfo.setSareaen(retValNode.get("sareaen").asText());
	    	ubikeInfo.setSnaen(retValNode.get("snaen").asText());
	    	ubikeInfo.setAren(retValNode.get("aren").asText());
	    	ubikeInfo.setBemp(retValNode.get("bemp").asInt());
	    	ubikeInfo.setAct(retValNode.get("act").asInt());
	    	
	    	result.addUBikeInfo(ubikeInfo);
	    }
		
		return result;
	}

}
