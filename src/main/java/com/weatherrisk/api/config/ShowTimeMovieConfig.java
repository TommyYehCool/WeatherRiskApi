package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.ShowTimeTheater;

@Configuration
public class ShowTimeMovieConfig {
	/**
	 * 場次基本網址
	 * 
	 * 最後可加: ?date=2017-03-16
	 */
	private final String MOVIE_TIMES_URL = "https://api.showtimes.com.tw/1/events/listForCorporation/{0}";
	/**
	 * 欣欣秀泰
	 */
	private String StarStarShowTimeUrl;
	/**
	 * 今日秀泰
	 */
	private String TodayShowTimeUrl;
	/**
	 * 板橋秀泰
	 */
	private String BanqiaoShowTimeUrl;
	/**
	 * 東南亞秀泰
	 */
	private String SoutheastShowTimeUrl;
	
	public ShowTimeMovieConfig() {
		StarStarShowTimeUrl = MessageFormat.format(MOVIE_TIMES_URL, ShowTimeTheater.STARSTAR.getCropId());
		TodayShowTimeUrl = MessageFormat.format(MOVIE_TIMES_URL, ShowTimeTheater.TODAY.getCropId());
		BanqiaoShowTimeUrl = MessageFormat.format(MOVIE_TIMES_URL, ShowTimeTheater.BANQIAO.getCropId());
		SoutheastShowTimeUrl = MessageFormat.format(MOVIE_TIMES_URL, ShowTimeTheater.SOUTHEAST.getCropId());
	}
	
	public String getStarStarShowTimeUrl() {
		return StarStarShowTimeUrl;
	}
	
	public String getTodayShowTimeUrl() {
		return TodayShowTimeUrl;
	}

	public String getBanqiaoShowTimeUrl() {
		return BanqiaoShowTimeUrl;
	}

	public String getSoutheastShowTimeUrl() {
		return SoutheastShowTimeUrl;
	}

}
