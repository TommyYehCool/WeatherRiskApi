package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.MiramarTheater;

@Configuration
public class MiramarMovieConfig {
	
	private final String BASE_URL = "https://www.miramarcinemas.com.tw/timetable.aspx?place={0}";
	/**
	 * 大直美麗華
	 */
	private String TachihMiramarUrl;
	
	public MiramarMovieConfig() {
		TachihMiramarUrl = MessageFormat.format(BASE_URL, MiramarTheater.TACHIH.getPlace());
	}

	public String getTachihMiramarUrl() {
		return TachihMiramarUrl;
	}

}
