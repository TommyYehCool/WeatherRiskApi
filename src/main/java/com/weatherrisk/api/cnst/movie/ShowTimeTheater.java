package com.weatherrisk.api.cnst.movie;

public enum ShowTimeTheater {
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

	public static boolean isSupportedTheater(String inputMsg) {
		for (ShowTimeTheater theater : ShowTimeTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static ShowTimeTheater convertByInputMsg(String inputMsg) {
		for (ShowTimeTheater theater : ShowTimeTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}

}
