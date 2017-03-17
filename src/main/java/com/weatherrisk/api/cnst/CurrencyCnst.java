package com.weatherrisk.api.cnst;

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
	
	LTC
	;
	
	private static EnumSet<CurrencyCnst> crypto_currency;
	
	private static EnumSet<CurrencyCnst> real_currency;
	
	static {
		crypto_currency 
			= EnumSet.of(CurrencyCnst.BTC, CurrencyCnst.ETH, CurrencyCnst.LTC);
		
		real_currency
			= EnumSet.complementOf(crypto_currency);
	}
	
	public static boolean isSupportedCurrency(String code) {
		for (CurrencyCnst currency : CurrencyCnst.values()) {
			if (currency.toString().compareToIgnoreCase(code) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCryptoCurrency(String code) {
		for (CurrencyCnst currency : crypto_currency) {
			if (currency.toString().compareToIgnoreCase(code) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isRealCurrency(String code) {
		for (CurrencyCnst currency : real_currency) {
			if (currency.toString().compareToIgnoreCase(code) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static CurrencyCnst convert(String code) {
		for (CurrencyCnst currency : CurrencyCnst.values()) {
			if (currency.toString().compareToIgnoreCase(code) == 0) {
				return currency;
			}
		}
		return null;
	}
}
