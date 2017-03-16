package com.weatherrisk.api.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "view_show_movies")
public class ViewShowMovie {
	
	private String theaterName;
	private String filmName;
	private List<MovieDateTime> movieDateTimes = new ArrayList<>();
	
	public void addMovieDateTime(MovieDateTime movieDateTime) {
		movieDateTimes.add(movieDateTime);
	}
}