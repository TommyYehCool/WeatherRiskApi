package com.weatherrisk.api.config.stock;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StockConfig {
	private final String TSE_STOCK_INFO_URL = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
	
	private final String OTC_STOCK_INFO_URL = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
	
	private final String TWSE_URL = "http://mis.twse.com.tw/stock";
	/**
	 * 最後面要加 System.currentTimeMillis()
	 */
	private final String PRICE_URL = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?json=1&delay=0&ex_ch={0}&_=";
	
	public String getTseStockInfoUrl() {
		return this.TSE_STOCK_INFO_URL;
	}
	
	public String getOtcStockInfoUrl() {
		return this.OTC_STOCK_INFO_URL;
	}
	
	public String getSessionIdUrl() {
		return TWSE_URL;
	}
	
	public String getPriceUrl(String ex_ch) {
		String url = PRICE_URL + System.currentTimeMillis();
		return MessageFormat.format(url, ex_ch);
	}
}
