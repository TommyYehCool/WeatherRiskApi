package com.weatherrisk.api.cnst.movie.theaters;

import com.weatherrisk.api.cnst.movie.MovieTheater;

public enum MiramarTheater implements MovieTheater {
	TACHIH("大直美麗華", "1");
	
	private String chineseName;
	private String place;
	
	private MiramarTheater(String chineseName, String place) {
		this.chineseName = chineseName;
		this.place = place;
	}

	@Override
	public String getChineseName() {
		return chineseName;
	}

	public String getPlace() {
		return place;
	}
	
	public static boolean isSupportedTheater(String input) {
		for (MiramarTheater e : MiramarTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static MiramarTheater convertByChineseNameStartWith(String input) {
		for (MiramarTheater e : MiramarTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static MiramarTheater convertByChineseNameEquals(String input) {
		for (MiramarTheater e : MiramarTheater.values()) {
			if (input.equals(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static MiramarTheater convertByEnumName(String input) {
		for (MiramarTheater e : MiramarTheater.values()) {
			if (input.equals(e.toString())) {
				return e;
			}
		}
		return null;
	}

}
