package com.weatherrisk.api.cnst.line;

import java.util.Arrays;
import java.util.List;

public enum LineFinancialFunction implements LineFunction {
	CRYPTO_CURRENCY(
		Arrays.asList("coin", "虛擬貨幣"),
		"虛擬貨幣",
		"虛擬貨幣",
		"提供下列功能",
		"虛擬貨幣",
		"/buttons/bitcoin.jpeg",
		CryptoCurrencySubFunction.values()
	);
	
	public static final String FINANCIAL_MENU_IMG_PATH = "/buttons/bitcoin.jpeg";
	public static final String FINANCIAL_MENU_TITLE = "金融功能";
	public static final String FINANCIAL_MENU_TEXT = "金融功能訊息";
	public static final String FINANCIAL_ALT_TEXT = "常用金融功能";
	
	private List<String> keywords;
	private String subItemName;
	private String subMenuTitle;
	private String subMeunText;
	private String subAltText;
	private String subImagePath;
	private LineSubFunction[] lineSubFuncs;
	
	private LineFinancialFunction(List<String> keywords, String subItemName, String subMenuTitle, String subMenuText, String subAltText, String subImagePath, LineSubFunction[] lineSubFuncs) {
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

	public static LineFinancialFunction convertByName(String name) {
		for (LineFinancialFunction e : LineFinancialFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public static LineFinancialFunction convertByKeyword(String keyword) {
		for (LineFinancialFunction e : LineFinancialFunction.values()) {
			if (e.getKeywords().contains(keyword)) {
				return e;
			}
		}
		return null;
	}
}
