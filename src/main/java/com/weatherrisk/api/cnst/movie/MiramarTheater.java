package com.weatherrisk.api.cnst.movie;

public enum MiramarTheater {
	TACHIH("大直美麗華", "1");
	
	private String chineseName;
	private String place;
	
	private MiramarTheater(String chineseName, String place) {
		this.chineseName = chineseName;
		this.place = place;
	}

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

}
