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
	
	public static boolean isSupportedTheater(String inputMsg) {
		for (MiramarTheater theater : MiramarTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static MiramarTheater convertByInputMsg(String inputMsg) {
		for (MiramarTheater theater : MiramarTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static MiramarTheater convertByChineseName(String inputMsg) {
		for (MiramarTheater theater : MiramarTheater.values()) {
			if (inputMsg.equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static MiramarTheater convertByName(String inputMsg) {
		for (MiramarTheater theater : MiramarTheater.values()) {
			if (theater.toString().equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}

}
