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
	 * 西門國賓
	 */
	private String XimenAmbassadorUrl;
	/**
	 * 微風國賓
	 */
	private String BreezeAmbassdorUrl;
	/**
	 * 晶冠國賓
	 */
	private String CrownAmbassadorUrl;
	
	public AmbassadorMovieConfig() {
		XimenAmbassadorUrl = MessageFormat.format(BASE_URL, AmbassadorTheater.XIMEN.getTheaterId());
		BreezeAmbassdorUrl = MessageFormat.format(BASE_URL, AmbassadorTheater.BREEZE.getTheaterId());
		CrownAmbassadorUrl = MessageFormat.format(BASE_URL, AmbassadorTheater.CROWN.getTheaterId());
	}

	public String getXimenAmbassadorUrl() {
		return XimenAmbassadorUrl;
	}

	public String getBreezeAmbassdorUrl() {
		return BreezeAmbassdorUrl;
	}

	public String getCrownAmbassadorUrl() {
		return CrownAmbassadorUrl;
	}
}
