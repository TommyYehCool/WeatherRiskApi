package com.weatherrisk.api.service.movie;

public interface MovieService {
	public void refreshMovieTimes();
	
	public void deleteAllMovieTimes();
	
	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName);
	
	public String queryNowPlayingByTheaterName(String theaterName);
	
	public void waitForCreateDatasThreadComplete();
}
