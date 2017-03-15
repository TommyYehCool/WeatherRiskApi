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

public class NewTaipeiUBikeAllInfoDeserializer extends JsonDeserializer<UBikeAllInfo> {

	@Override
	public UBikeAllInfo deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		UBikeAllInfo result = new UBikeAllInfo();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    Iterator<JsonNode> itValNodes = rootNode.elements();
	    while (itValNodes.hasNext()) {
	    	UBikeInfo ubikeInfo = new UBikeInfo();
	    	
	    	JsonNode valNode = itValNodes.next();
	    	
	    	ubikeInfo.setSno(valNode.get("sno").asText());
	    	ubikeInfo.setSna(valNode.get("sna").asText());
	    	ubikeInfo.setTot(valNode.get("tot").asInt());
	    	ubikeInfo.setSbi(valNode.get("sbi").asInt());
	    	ubikeInfo.setSarea(valNode.get("sarea").asText());
	    	ubikeInfo.setMday(valNode.get("mday").asText());
	    	ubikeInfo.setLat(valNode.get("lat").asDouble());
	    	ubikeInfo.setLng(valNode.get("lng").asDouble());
	    	ubikeInfo.setAr(valNode.get("ar").asText());
	    	ubikeInfo.setSareaen(valNode.get("sareaen").asText());
	    	ubikeInfo.setSnaen(valNode.get("snaen").asText());
	    	ubikeInfo.setAren(valNode.get("aren").asText());
	    	ubikeInfo.setBemp(valNode.get("bemp").asInt());
	    	ubikeInfo.setAct(valNode.get("act").asInt());
	    	
	    	result.addUBikeInfo(ubikeInfo);
	    }
		
		return result;
	}

}
