package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.MovieDateTime;
import com.weatherrisk.api.model.ShowTimeMovie;
import com.weatherrisk.api.vo.json.showtime.ShowTimeAllMoviesInfo;

public class ShowTimeAllMovieTimesInfoDeserializer extends JsonDeserializer<ShowTimeAllMoviesInfo> {

	@Override
	public ShowTimeAllMoviesInfo deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ShowTimeAllMoviesInfo result = new ShowTimeAllMoviesInfo();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    JsonNode payloadNode = rootNode.get("payload");
	    
	    // 紀錄 id 對應電影名稱
	    Map<String, String> idFilmNameMap = new HashMap<>();
	    
	    // 處理基本資料
	    Iterator<JsonNode> itProgNodes = payloadNode.get("programs").elements();
	    while (itProgNodes.hasNext()) {
	    	JsonNode progNode = itProgNodes.next();
	    	
	    	String programId = progNode.get("id").asText();
	    	String filmName = progNode.get("name").asText();
	    	
	    	idFilmNameMap.put(programId, filmName);
	    }
	    
	    // 紀錄已存在的 ShowTimeMovie
	    Map<String, ShowTimeMovie> showTimeMovieMap = new HashMap<>();
	    
	    // 處理場次時間
	    ShowTimeMovie showTimeMovie = null;
	    
	    Iterator<JsonNode> itEventNodes = payloadNode.get("events").elements();
	    while (itEventNodes.hasNext()) {
	    	JsonNode eventNode = itEventNodes.next();
	    	
	    	String programId = eventNode.get("programId").asText();
	    	
	    	if (showTimeMovieMap.containsKey(programId)) {
	    		showTimeMovie = showTimeMovieMap.get(programId);
	    	}
	    	else {
	    		showTimeMovie = new ShowTimeMovie();
	    		String filmName = idFilmNameMap.get(programId);
	    		showTimeMovie.setFilmName(filmName);

	    		showTimeMovieMap.put(programId, showTimeMovie);
	    	}
	    	
	    	String externalUrl = eventNode.get("meta").get("externalUrl").asText();
	    	String dataToExtract = externalUrl.substring(externalUrl.indexOf("?") + 1, externalUrl.length());
	    	String[] datas = dataToExtract.split("&");
	    	
	    	String date = "";
	    	String time = "";
	    	for (String data : datas) {
	    		if (data.contains("selShowDate")) {
	    			date = data.substring(data.indexOf("=") + 1, data.length());
	    		}
	    		else if (data.contains("selShowTime")) {
	    			time = data.substring(data.indexOf("=") + 1, data.length());
	    		}
	    	}
	    	
	    	showTimeMovie.addMovieDateTime(new MovieDateTime(date, time));
	    }
	    
	    for (ShowTimeMovie showTimeMovieMapVals: showTimeMovieMap.values()) {
	    	result.addShowTimeMovie(showTimeMovieMapVals);
	    }
		return result;
	}
}
