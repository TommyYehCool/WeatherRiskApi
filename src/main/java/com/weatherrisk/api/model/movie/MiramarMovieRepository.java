package com.weatherrisk.api.model.movie;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MiramarMovieRepository extends MongoRepository<MiramarMovie, String> {

	List<MiramarMovie> findByTheaterName(String theaterName);

	List<MiramarMovie> findByTheaterNameAndFilmNameLike(String theaterName, String filmName);

}
