package com.weatherrisk.api.cnst;

public enum ViewshowTheather {
	XINYI("信義威秀", "TP"),
	
	QSQUARE("京站威秀", "QS"),
	
	SUN("日新威秀", "XM");
	
	private String chineseName;
	private String cid;
	
	private ViewshowTheather(String chineseName, String cid) {
		this.chineseName = chineseName;
		this.cid = cid;
	}
	
	public String getChineseName() {
		return this.chineseName;
	}
	
	public String getCid() {
		return this.cid;
	}
}
