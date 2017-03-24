package com.weatherrisk.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WovieMovieConfig {
	/**
	 * 天母華威
	 */
	private String TienmouWovieUrl = "https://tienmou.woviecinemas.com.tw/movie_wovie.php";

	public String getTienmouWovieUrl() {
		return TienmouWovieUrl;
	}

}
