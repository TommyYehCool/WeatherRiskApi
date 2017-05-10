package com.weatherrisk.api.cnst.line;

import java.util.Arrays;
import java.util.List;

public enum LineFunction {
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
	),
	CRYPTO_CURRENCY(
		Arrays.asList("coin", "虛擬貨幣"),
		"虛擬貨幣",
		"虛擬貨幣",
		"提供下列功能",
		"虛擬貨幣",
		"/buttons/bitcoin.jpeg",
		CryptoCurrencySubFunction.values()
	) 
	;
	
	public static final String MAIN_MENU_IMG_PATH = "/buttons/bitcoin.jpeg";
	public static final String MAIN_MENU_TITLE = "我是全能機器人";
	public static final String MAIN_MENU_TEXT = "我提供下列功能";
	public static final String MAIN_ALT_TEXT = "全能機器人";

	private List<String> keywords;
	private String subItemName;
	private String subMenuTitle;
	private String subMeunText;
	private String subAltText;
	private String subImagePath;
	private LineSubFunction[] lineSubFuncs;
	
	private LineFunction(List<String> keywords, String subItemName, String subMenuTitle, String subMenuText, String subAltText, String subImagePath, LineSubFunction[] lineSubFuncs) {
		this.keywords = keywords;
		this.subItemName = subItemName;
		this.subMenuTitle = subMenuTitle;
		this.subMeunText = subMenuText;
		this.subAltText = subAltText;
		this.subImagePath = subImagePath;
		this.lineSubFuncs = lineSubFuncs;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}
	
	public String getSubItemName() {
		return subItemName;
	}
	
	public String getSubMenuTitle() {
		return subMenuTitle;
	}

	public String getSubMenuText() {
		return subMeunText;
	}
	
	public String getSubAltText() {
		return subAltText;
	}

	public String getSubImagePath() {
		return subImagePath;
	}

	public LineSubFunction[] getLineSubFuncs() {
		return lineSubFuncs;
	}

	public static LineFunction convertByName(String name) {
		for (LineFunction e : LineFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public static LineFunction convertByKeyword(String keyword) {
		for (LineFunction e : LineFunction.values()) {
			if (e.getKeywords().contains(keyword)) {
				return e;
			}
		}
		return null;
	}
}
