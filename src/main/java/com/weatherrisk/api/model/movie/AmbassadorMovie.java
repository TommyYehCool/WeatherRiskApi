package com.weatherrisk.api.model.movie;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ambassador_movies")
public class AmbassadorMovie {

	private String theaterName;
	private String filmName;
	private List<MovieDateTime> movieDateTimes = new ArrayList<>();
	
	public void addMovieDateTime(MovieDateTime movieDateTime) {
		movieDateTimes.add(movieDateTime);
	}
}
