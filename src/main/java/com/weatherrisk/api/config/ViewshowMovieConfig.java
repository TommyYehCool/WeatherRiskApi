package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.ViewshowTheather;

@Configuration
public class ViewshowMovieConfig {
	
	private final String BASE_URL = "http://www.vscinemas.com.tw/visPrintShowTimes.aspx?cid={0}&visLang=2";
	/**
	 * 信義
	 */
	private String XinyiViewshowUrl;
	/**
	 * 京站
	 */
	private String QSquareViewshowUrl;
	/**
	 * 日新
	 */
	private String SunViewshowUrl;
	
	public ViewshowMovieConfig() {
		XinyiViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheather.XINYI.getCid());
		QSquareViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheather.QSQUARE.getCid());
		SunViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheather.SUN.getCid());
	}

	public String getXinyiViewshowUrl() {
		return XinyiViewshowUrl;
	}

	public String getQSquareViewshowUrl() {
		return QSquareViewshowUrl;
	}

	public String getSunViewshowUrl() {
		return SunViewshowUrl;
	}
}
