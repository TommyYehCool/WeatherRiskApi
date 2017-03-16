package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.ShowTimeTheater;

@Configuration
public class ShowTimeMovieConfig {
	
	private final String BASE_URL = "https://www.showtimes.com.tw/events?corpId={6}";
	/**
	 * 板橋秀泰
	 */
	private String BanqiaoShowTimeUrl;
	
	public ShowTimeMovieConfig() {
		BanqiaoShowTimeUrl = MessageFormat.format(BASE_URL, ShowTimeTheater.BANQIAO.getCropId());
	}

	public String getBanqiaoShowTimeUrl() {
		return BanqiaoShowTimeUrl;
	}

}
