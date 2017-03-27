package com.weatherrisk.api.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorMovieRepository extends MongoRepository<AmbassadorMovie, String> {

	List<AmbassadorMovie> findByTheaterName(String theaterName);

	List<AmbassadorMovie> findByTheaterNameAndFilmNameLike(String theaterName, String filmName);

}
