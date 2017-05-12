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
	
	public static boolean isSupportedTheater(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static ViewshowTheater convertByInputMsg(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static ViewshowTheater convertByChineseName(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (inputMsg.equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static ViewshowTheater convertByName(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (theater.toString().equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
}
