package com.weatherrisk.api.cnst;

public enum ViewshowTheater {
	XINYI("信義威秀", "TP"),
	
	QSQUARE("京站威秀", "QS"),
	
	SUN("日新威秀", "XM");
	
	private String chineseName;
	private String cid;
	
	private ViewshowTheater(String chineseName, String cid) {
		this.chineseName = chineseName;
		this.cid = cid;
	}
	
	public String getChineseName() {
		return this.chineseName;
	}
	
	public String getCid() {
		return this.cid;
	}
	
	public static boolean isSupportedTheater(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return true;
			}
		}
		return false;
	}
	
	public static ViewshowTheater convertByInputMsg(String inputMsg) {
		for (ViewshowTheater theater : ViewshowTheater.values()) {
			if (inputMsg.startsWith(theater.getChineseName())) {
				return theater;
			}
		}
		return null;
	}
}
