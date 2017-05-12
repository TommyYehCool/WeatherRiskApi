package com.weatherrisk.api.cnst.currency;

import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;

public enum CryptoCurrencyExchange {
	BTCE("BTC-E", BTCEExchange.class.getName()),
	POLONIEX("Poloniex", PoloniexExchange.class.getName());
	
	private String exchangeName;
	private String exchangeClassName;
	
	private CryptoCurrencyExchange(String exchangeName, String exchangeClassName) {
		this.exchangeName = exchangeName;
		this.exchangeClassName = exchangeClassName;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public String getExchangeClassName() {
		return exchangeClassName;
	}
}
