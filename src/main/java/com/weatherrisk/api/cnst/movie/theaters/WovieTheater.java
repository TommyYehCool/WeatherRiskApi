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

	public static boolean isSupportedTheater(String input) {
		for (WovieTheater e : WovieTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static WovieTheater convertByChineseNameStartWith(String input) {
		for (WovieTheater e : WovieTheater.values()) {
			if (input.startsWith(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static WovieTheater convertByChineseNameEquals(String input) {
		for (WovieTheater e : WovieTheater.values()) {
			if (input.equals(e.getChineseName())) {
				return e;
			}
		}
		return null;
	}
	
	public static WovieTheater convertByName(String input) {
		for (WovieTheater e : WovieTheater.values()) {
			if (input.equals(e.toString())) {
				return e;
			}
		}
		return null;
	}
	
}
