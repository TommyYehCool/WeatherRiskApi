package com.weatherrisk.api.model.movie;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WovieMovieRepository extends MongoRepository<WovieMovie, String> {
	
	List<WovieMovie> findByTheaterName(String theaterName);
	
	List<WovieMovie> findByTheaterNameAndFilmNameLike(String theaterName, String filmName);

}
