package com.weatherrisk.api.cnst.currency;

import java.util.EnumSet;

public enum CurrencyCnst {
	// ----- Real Currency -----
	USD,
	
	HKD,
	
	GBP,
	
	AUD,
	
	CAD,
	
	SGD,
	
	CHF,
	
	JPY,
	
	ZAR,
	
	SEK,
	
	NZD,
	
	THB,
	
	PHP,
	
	IDR,
	
	EUR,
	
	KRW,
	
	VND,
	
	MYR,
	
	CNY,
	
	// ----- Crypto Currency -----
	BTC,
	
	ETH,
	
	LTC,
	
	STR,
	
	XRP
	;
	
	private static EnumSet<CurrencyCnst> crypto_currency;
	
	private static EnumSet<CurrencyCnst> poloneix_crypto_currency;
	
	private static EnumSet<CurrencyCnst> real_currency;
	
	static {
		crypto_currency 
			= EnumSet.of(CurrencyCnst.BTC, CurrencyCnst.ETH, CurrencyCnst.LTC, CurrencyCnst.STR, CurrencyCnst.XRP);
		
		poloneix_crypto_currency
			= EnumSet.of(CurrencyCnst.ETH, CurrencyCnst.LTC, CurrencyCnst.STR, CurrencyCnst.XRP);
		
		real_currency
			= EnumSet.complementOf(crypto_currency);
	}
	
	public static boolean isSupportedCurrency(String code) {
		for (CurrencyCnst currency : CurrencyCnst.values()) {
			if (currency.toString().equalsIgnoreCase(code)) {
				return true;
			}
		}
		return false;
	}
	
	public static CurrencyCnst[] getCryptoCurrency() {
		return crypto_currency.toArray(new CurrencyCnst[0]);
	}
	
	public static boolean isCryptoCurrency(String code) {
		for (CurrencyCnst currency : crypto_currency) {
			if (currency.toString().equalsIgnoreCase(code)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTreasurySupportedCryptoCurrency(String code) {
		for (CurrencyCnst currency : poloneix_crypto_currency) {
			if (currency.toString().equalsIgnoreCase(code)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getSupportedCryptoCurrency() {
		StringBuilder buffer = new StringBuilder();
		for (CurrencyCnst currency : crypto_currency) {
			buffer.append(currency.toString());
			buffer.append(", ");
		}
		return buffer.toString().substring(0, buffer.toString().length() - 2);
	}
	
	public static String getTreasurySupportedCryptoCurrency() {
		StringBuilder buffer = new StringBuilder();
		for (CurrencyCnst currency : poloneix_crypto_currency) {
			buffer.append(currency.toString());
			buffer.append(", ");
		}
		return buffer.toString().substring(0, buffer.toString().length() - 2);
	}
	
	public static boolean isRealCurrency(String code) {
		for (CurrencyCnst currency : real_currency) {
			if (currency.toString().equalsIgnoreCase(code)) {
				return true;
			}
		}
		return false;
	}
	
	public static CurrencyCnst convert(String code) {
		for (CurrencyCnst currency : CurrencyCnst.values()) {
			if (currency.toString().equalsIgnoreCase(code)) {
				return currency;
			}
		}
		return null;
	}
}
