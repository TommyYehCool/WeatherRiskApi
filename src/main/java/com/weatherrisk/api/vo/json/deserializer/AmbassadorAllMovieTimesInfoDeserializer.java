package com.weatherrisk.api.vo.json.deserializer;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.weatherrisk.api.model.movie.AmbassadorMovie;
import com.weatherrisk.api.model.movie.MovieDateTime;
import com.weatherrisk.api.vo.json.ambassador.AmbassadorAllMoviesInfo;

public class AmbassadorAllMovieTimesInfoDeserializer extends JsonDeserializer<AmbassadorAllMoviesInfo> {

	@Override
	public AmbassadorAllMoviesInfo deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		AmbassadorAllMoviesInfo result = new AmbassadorAllMoviesInfo();
		
		ObjectCodec oc = jp.getCodec();
	    JsonNode rootNode = oc.readTree(jp);
	    
	    Iterator<JsonNode> itFilmNodes = rootNode.iterator();
	    while (itFilmNodes.hasNext()) {
	    	AmbassadorMovie movie = new AmbassadorMovie(); 
	    	
	    	JsonNode filmNode = itFilmNodes.next();
	    	
	    	JsonNode periodShowTimeArray = filmNode.get("PeriodShowtime");
	    	
	    	JsonNode periodShowTime = periodShowTimeArray.get(0);
	    	
	    	String filmName = periodShowTime.get("AssistantName").asText();
	    	movie.setFilmName(filmName);
	    	
	    	String playingDate = periodShowTime.get("PlayingDate").asText();
	    	playingDate = playingDate.substring(0, playingDate.indexOf("T"));
	    	
	    	StringBuilder sessionBuilder = new StringBuilder();
	    	Iterator<JsonNode> itShowTimes = periodShowTime.get("Showtimes").iterator();
	    	while (itShowTimes.hasNext()) {
	    		String showTime = itShowTimes.next().asText();
	    		sessionBuilder.append(showTime).append(",");
	    	}
	    	String session = sessionBuilder.toString();
	    	session = session.substring(0, session.length() - 1);
	    	movie.addMovieDateTime(new MovieDateTime(playingDate, session));
	    	
	    	result.addAmbassadorMovie(movie);
	    }
		
		return result;
	}

}
