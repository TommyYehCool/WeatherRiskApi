package com.weatherrisk.api.cnst;

public enum WovieTheater {
	TIENMOU("天母華威");
	
	private String chineseName;
	
	private WovieTheater(String chineseName) {
		this.chineseName = chineseName;
	}

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
}
