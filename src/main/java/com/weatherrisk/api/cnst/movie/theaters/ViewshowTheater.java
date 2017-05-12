package com.weatherrisk.api.cnst.movie.theaters;

import com.weatherrisk.api.cnst.movie.MovieTheater;

public enum ViewshowTheater implements MovieTheater {
	XINYI("信義威秀", "TP"),
	
	QSQUARE("京站威秀", "QS"),
	
	SUN("日新威秀", "XM"),
	
	MEGA_CITY("板橋大遠百威秀", "BQ");
	
	private String chineseName;
	private String cid;
	
	private ViewshowTheater(String chineseName, String cid) {
		this.chineseName = chineseName;
		this.cid = cid;
	}
	
	@Override
	public String getChineseName() {
		return this.chineseName;
	}
	
	public String getCid() {
		return this.cid;
	}
	
	public static boolean isSupportedTheater(String input) {
		for (ViewshowTheater e : ViewshowTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static ViewshowTheater convertByChineseNameStartWith(String input) {
		for (ViewshowTheater e : ViewshowTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static ViewshowTheater convertByChineseNameEquals(String input) {
		for (ViewshowTheater e : ViewshowTheater.values()) {
			if (input.equals(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static ViewshowTheater convertByEnumName(String input) {
		for (ViewshowTheater e : ViewshowTheater.values()) {
			if (input.equals(e.toString())) {
				return e;
			}
		}
		return null;
	}
	
}
