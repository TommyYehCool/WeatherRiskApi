package com.weatherrisk.api.config.movie;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

import com.weatherrisk.api.cnst.ViewshowTheater;

@Configuration
public class ViewshowMovieConfig {
	
	private final String BASE_URL = "http://www.vscinemas.com.tw/visPrintShowTimes.aspx?cid={0}&visLang=2";
	/**
	 * 信義威秀
	 */
	private String XinyiViewshowUrl;
	/**
	 * 京站威秀
	 */
	private String QSquareViewshowUrl;
	/**
	 * 日新威秀
	 */
	private String SunViewshowUrl;
	/**
	 * 板橋大遠百威秀
	 */
	private String MegaCityViewshowUrl;
	
	public ViewshowMovieConfig() {
		XinyiViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheater.XINYI.getCid());
		QSquareViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheater.QSQUARE.getCid());
		SunViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheater.SUN.getCid());
		MegaCityViewshowUrl = MessageFormat.format(BASE_URL, ViewshowTheater.MEGA_CITY.getCid());
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

	public String getMegaCityViewshowUrl() {
		return MegaCityViewshowUrl;
	}

}
