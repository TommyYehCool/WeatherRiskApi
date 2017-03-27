package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.movie.MovieDateTime;
import com.weatherrisk.api.model.movie.ShowTimeMovie;
import com.weatherrisk.api.vo.json.showtime.ShowTimeAllMoviesInfo;

public class ShowTimeAllMovieTimesInfoDeserializer extends JsonDeserializer<ShowTimeAllMoviesInfo> {

	@Override
	public ShowTimeAllMoviesInfo deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ShowTimeAllMoviesInfo result = new ShowTimeAllMoviesInfo();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
//	    System.out.println(rootNode);
	    
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
	    	
	    	String startedAt = eventNode.get("startedAt").asText();
	    	String date = startedAt.substring(0, startedAt.indexOf("T"));
	    	String time = convertToGmt8(startedAt.substring(startedAt.indexOf("T") + 1, startedAt.indexOf(".")));
	    	
	    	showTimeMovie.addMovieDateTime(new MovieDateTime(date, time));
	    }
	    
	    for (ShowTimeMovie showTimeMovieMapVals: showTimeMovieMap.values()) {
	    	result.addShowTimeMovie(showTimeMovieMapVals);
	    }
		return result;
	}

	private String convertToGmt8(String gmtZero) {
		SimpleDateFormat gmtTimeFormat = new SimpleDateFormat("HH:mm:ss");
		gmtTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		SimpleDateFormat gmtAdd8TimeFormat = new SimpleDateFormat("HH:mm");
		gmtAdd8TimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		try {
			Date gmtDate = gmtTimeFormat.parse(gmtZero);
			return gmtAdd8TimeFormat.format(gmtDate);
		} catch (ParseException e) {
			return "??:??";
		}
	}
}
