package com.weatherrisk.api.cnst.movie.theaters;

import com.weatherrisk.api.cnst.movie.MovieTheater;

public enum ShowTimeTheater implements MovieTheater {
	STARSTAR("欣欣秀泰", "2"),

	TODAY("今日秀泰", "4"),

	BANQIAO("板橋秀泰", "6"),

	SOUTHEAST("東南亞秀泰", "8");
	
	private String chineseName;
	private String cropId;

	private ShowTimeTheater(String chineseName, String cropId) {
		this.chineseName = chineseName;
		this.cropId = cropId;
	}

	public String getChineseName() {
		return chineseName;
	}

	public String getCropId() {
		return cropId;
	}

	public static boolean isSupportedTheater(String input) {
		for (ShowTimeTheater e : ShowTimeTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static ShowTimeTheater convertByChineseNameStartWith(String input) {
		for (ShowTimeTheater e : ShowTimeTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static ShowTimeTheater convertByChineseNameEquals(String input) {
		for (ShowTimeTheater e : ShowTimeTheater.values()) {
			if (input.equals(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static ShowTimeTheater convertByEnumName(String input) {
		for (ShowTimeTheater e : ShowTimeTheater.values()) {
			if (input.equals(e.toString())) {
				return e;
			}
		}
		return null;
	}

}
