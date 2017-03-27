package com.weatherrisk.api.config.movie;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.AmbassadorTheater;

@Configuration
public class AmbassadorMovieConfig {
	/**
	 * 場次基本網址
	 * 
	 * 最後可加: &showingDate=2017/03/27
	 */
	private final String BASE_URL = "http://cinemaservice.ambassador.com.tw/ambassadorsite.webapi/api/Movies/GetShowtimeListForTheater/?theaterId={0}";
	/**
	 * 晶冠國賓
	 */
	private String CrownAmbassadorUrl;
	
	public AmbassadorMovieConfig() {
		CrownAmbassadorUrl = MessageFormat.format(BASE_URL, AmbassadorTheater.CROWN.getTheaterId());
	}

	public String getCrownAmbassadorUrl() {
		return CrownAmbassadorUrl;
	}
}
