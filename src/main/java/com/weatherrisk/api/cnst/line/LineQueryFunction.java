package com.weatherrisk.api.cnst.line;

import java.util.Arrays;
import java.util.List;

public enum LineQueryFunction implements LineFunction {
	PARKING_LOT_INFO(
		Arrays.asList("park", "停車"),
		"停車資訊",
		"停車資訊查詢(提供台北市及新北市)", 
		"提供下列功能", 
		"停車場資訊",
		"/buttons/bitcoin.jpeg",
		ParkingLotSubFunction.values()
	),
	WEATHER(
		Arrays.asList("weather", "天氣"),
		"天氣資訊",
		"天氣資訊查詢",
		"提供下列功能",
		"天氣資訊",
		"/buttons/bitcoin.jpeg",
		WeatherSubFunction.values()
	),
	RECEIPT_REWARD(
		Arrays.asList("receipt", "發票"),
		"統一發票",
		"統一發票",
		"提供下列功能",
		"統一發票",
		"/buttons/bitcoin.jpeg",
		ReceiptRewardSubFunction.values()
	)
	;
	
	public static final String QUERY_MENU_IMG_PATH = "/buttons/bitcoin.jpeg";
	public static final String QUERY_MENU_TITLE = "查詢功能";
	public static final String QUERY_MENU_TEXT = "查詢功能訊息";
	public static final String QUERY_ALT_TEXT = "提供生活常用查詢功能";

	private List<String> keywords;
	private String subItemName;
	private String subMenuTitle;
	private String subMeunText;
	private String subAltText;
	private String subImagePath;
	private LineSubFunction[] lineSubFuncs;
	
	private LineQueryFunction(List<String> keywords, String subItemName, String subMenuTitle, String subMenuText, String subAltText, String subImagePath, LineSubFunction[] lineSubFuncs) {
		this.keywords = keywords;
		this.subItemName = subItemName;
		this.subMenuTitle = subMenuTitle;
		this.subMeunText = subMenuText;
		this.subAltText = subAltText;
		this.subImagePath = subImagePath;
		this.lineSubFuncs = lineSubFuncs;
	}
	
	@Override
	public List<String> getKeywords() {
		return keywords;
	}
	
	@Override
	public String getSubItemName() {
		return subItemName;
	}
	
	@Override
	public String getSubMenuTitle() {
		return subMenuTitle;
	}

	@Override
	public String getSubMenuText() {
		return subMeunText;
	}
	
	@Override
	public String getSubAltText() {
		return subAltText;
	}

	@Override
	public String getSubImagePath() {
		return subImagePath;
	}

	@Override
	public LineSubFunction[] getLineSubFuncs() {
		return lineSubFuncs;
	}

	public static LineQueryFunction convertByName(String name) {
		for (LineQueryFunction e : LineQueryFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public static LineQueryFunction convertByKeyword(String keyword) {
		for (LineQueryFunction e : LineQueryFunction.values()) {
			if (e.getKeywords().contains(keyword)) {
				return e;
			}
		}
		return null;
	}
}
