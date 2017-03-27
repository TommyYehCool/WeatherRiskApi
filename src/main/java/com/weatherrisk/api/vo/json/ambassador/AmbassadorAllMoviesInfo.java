package com.weatherrisk.api.vo.json.ambassador;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weatherrisk.api.model.AmbassadorMovie;
import com.weatherrisk.api.vo.json.deserializer.AmbassadorAllMovieTimesInfoDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = AmbassadorAllMovieTimesInfoDeserializer.class)
@Data
@NoArgsConstructor
public class AmbassadorAllMoviesInfo {
	private List<AmbassadorMovie> ambassadorMovies = new ArrayList<>();
	
	public void addAmbassadorMovie(AmbassadorMovie ambassadorMovie) {
		this.ambassadorMovies.add(ambassadorMovie);
	}
}
