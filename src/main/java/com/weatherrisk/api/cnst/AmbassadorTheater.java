package com.weatherrisk.api.cnst;

public enum AmbassadorTheater {
	CROWN("晶冠國賓", "3301d822-b385-4aa8-a9eb-aa59d58e95c9");
	
	private String chineseName;
	private String theaterId;
	
	private AmbassadorTheater(String chineseName, String theaterId) {
		this.chineseName = chineseName;
		this.theaterId = theaterId;
	}
	
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
	
}	
