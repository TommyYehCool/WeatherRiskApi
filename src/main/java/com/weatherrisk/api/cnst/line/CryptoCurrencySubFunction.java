package com.weatherrisk.api.cnst.line;

public enum CryptoCurrencySubFunction implements LineSubFunction {
	HIT_PRICE_INFO("虛擬貨幣到價資訊"),
	QUERY_CRYPTO_CURRENCT_TREASURY("查詢虛擬貨幣庫存");

	private String label;
	
	private CryptoCurrencySubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static CryptoCurrencySubFunction convertByMsg(String msg) {
		for (CryptoCurrencySubFunction e : CryptoCurrencySubFunction.values()) {
			if (e.toString().equalsIgnoreCase(msg)) {
				return e;
			}
		}
		return null;
	}
}
