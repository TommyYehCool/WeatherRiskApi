package com.weatherrisk.api.cnst.line;

import java.util.Arrays;
import java.util.List;

public enum LineFunction {
	PARKING_LOT_INFO(
		Arrays.asList("park", "停車"), 
		"停車資訊查詢(提供台北市及新北市)", 
		"提供下列功能", 
		"/buttons/bitcoin.jpeg",
		ParkingLotSubFunction.values(),
		"停車場資訊"
	),
	WEATHER(
		Arrays.asList("weather", "天氣"),
		"天氣資訊查詢",
		"提供下列功能",
		"/buttons/bitcoin.jpeg",
		WeatherSubFunction.values(),
		"天氣資訊"
	),
	CRYPTO_CURRENCY(
		Arrays.asList("coin", "虛擬貨幣"),
		"虛擬貨幣",
		"提供下列功能",
		"/buttons/bitcoin.jpeg",
		CryptoCurrencySubFunction.values(),
		"虛擬貨幣"
	);
	
	private List<String> keywords;
	private String title;
	private String text;
	private String imagePath;
	private LineSubFunction[] lineSubFuncs;
	private String altText;
	
	private LineFunction(List<String> keywords, String title, String text, String imagePath, LineSubFunction[] lineSubFuncs, String altText) {
		this.keywords = keywords;
		this.title = title;
		this.text = text;
		this.imagePath = imagePath;
		this.lineSubFuncs = lineSubFuncs;
		this.altText = altText;
	}
	
	public List<String> getKeywords() {
		return this.keywords;
	}
	
	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getImagePath() {
		return imagePath;
	}

	public LineSubFunction[] getLineSubFuncs() {
		return lineSubFuncs;
	}

	public String getAltText() {
		return altText;
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
