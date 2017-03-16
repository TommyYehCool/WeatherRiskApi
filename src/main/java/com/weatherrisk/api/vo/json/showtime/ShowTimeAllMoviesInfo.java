package com.weatherrisk.api.vo.json.showtime;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weatherrisk.api.model.ShowTimeMovie;
import com.weatherrisk.api.vo.json.deserializer.ShowTimeAllMovieTimesInfoDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = ShowTimeAllMovieTimesInfoDeserializer.class)
@Data
@NoArgsConstructor
public class ShowTimeAllMoviesInfo {

	private List<ShowTimeMovie> showTimeMovies = new ArrayList<>();
	
	public void addShowTimeMovie(ShowTimeMovie showTimeMovie) {
		this.showTimeMovies.add(showTimeMovie);
	}
}
