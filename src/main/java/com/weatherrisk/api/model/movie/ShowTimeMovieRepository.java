package com.weatherrisk.api.model.movie;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowTimeMovieRepository extends MongoRepository<ShowTimeMovie, String> {

	List<ShowTimeMovie> findByTheaterName(String theaterName);

	List<ShowTimeMovie> findByTheaterNameAndFilmNameLike(String theaterName, String filmName);

}
