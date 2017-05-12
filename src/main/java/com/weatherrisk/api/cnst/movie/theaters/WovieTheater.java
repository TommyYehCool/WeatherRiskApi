package com.weatherrisk.api.cnst.movie.theaters;

import com.weatherrisk.api.cnst.movie.MovieTheater;

public enum WovieTheater implements MovieTheater {
	TIENMOU("天母華威");
	
	private String chineseName;
	
	private WovieTheater(String chineseName) {
		this.chineseName = chineseName;
	}

	@Override
	public String getChineseName() {
		return chineseName;
	}

	public static boolean isSupportedTheater(String inputMsg) {
		for (WovieTheater theater : WovieTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static WovieTheater convertByInputMsg(String inputMsg) {
		for (WovieTheater theater : WovieTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static WovieTheater convertByChineseName(String inputMsg) {
		for (WovieTheater theater : WovieTheater.values()) {
			if (inputMsg.equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
	public static WovieTheater convertByName(String inputMsg) {
		for (WovieTheater theater : WovieTheater.values()) {
			if (theater.toString().equals(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
	
}
