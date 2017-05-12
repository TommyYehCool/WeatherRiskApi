package com.weatherrisk.api.cnst.movie.theaters;

import com.weatherrisk.api.cnst.movie.MovieTheater;

public enum AmbassadorTheater implements MovieTheater {
	XIMEN("西門國賓", "84b87b82-b936-4a39-b91f-e88328d33b4e"),
	BREEZE("微風國賓", "5c2d4697-7f54-4955-800c-7b3ad782582c"),
	CROWN("晶冠國賓", "3301d822-b385-4aa8-a9eb-aa59d58e95c9");
	
	private String chineseName;
	private String theaterId;
	
	private AmbassadorTheater(String chineseName, String theaterId) {
		this.chineseName = chineseName;
		this.theaterId = theaterId;
	}
	
	@Override
	public String getChineseName() {
		return chineseName;
	}

	public String getTheaterId() {
		return theaterId;
	}
	
	public static boolean isSupportedTheater(String inputMsg) {
		for (AmbassadorTheater theater : AmbassadorTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static AmbassadorTheater convertByInputMsg(String inputMsg) {
		for (AmbassadorTheater theater : AmbassadorTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static AmbassadorTheater convertByChineseName(String inputMsg) {
		for (AmbassadorTheater theater : AmbassadorTheater.values()) {
			if (inputMsg.equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static AmbassadorTheater convertByName(String inputMsg) {
		for (AmbassadorTheater theater : AmbassadorTheater.values()) {
			if (theater.toString().equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
}	
