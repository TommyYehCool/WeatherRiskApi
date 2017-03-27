package com.weatherrisk.api.model.movie;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ViewShowMovieRepository extends MongoRepository<ViewShowMovie, String> {
	
	List<ViewShowMovie> findByTheaterName(String theaterName);
	
	List<ViewShowMovie> findByTheaterNameAndFilmNameLike(String theaterName, String filmName);

}
