package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.ShowTimeTheater;

@Configuration
public class ShowTimeMovieConfig {
	/**
	 * 場次基本網址
	 * 
	 * 最後可加: '?date=2017-03-16'
	 */
	private final String MOVIE_TIMES_URL = "https://api.showtimes.com.tw/1/events/listForCorporation/{0}";
	/**
	 * 板橋秀泰
	 */
	private String BanqiaoShowTimeUrl;
	
	public ShowTimeMovieConfig() {
		BanqiaoShowTimeUrl = MessageFormat.format(MOVIE_TIMES_URL, ShowTimeTheater.BANQIAO.getCropId());
	}

	public String getMovieInfo_BanqiaoShowTimeUrl() {
		return BanqiaoShowTimeUrl;
	}

}
