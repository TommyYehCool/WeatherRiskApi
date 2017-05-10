package com.weatherrisk.api.cnst.line;

public enum CryptoCurrencySubFunction implements LineSubFunction {
	QUERY_CRYPTO_CURRENCY_PRICE("查詢虛擬貨幣匯率"),
	HIT_PRICE_INFO("查詢註冊虛擬貨幣到價資訊"),
	QUERY_CRYPTO_CURRENCT_TREASURY("查詢虛擬貨幣庫存");

	private String label;
	
	private CryptoCurrencySubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static CryptoCurrencySubFunction convertByName(String name) {
		for (CryptoCurrencySubFunction e : CryptoCurrencySubFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
