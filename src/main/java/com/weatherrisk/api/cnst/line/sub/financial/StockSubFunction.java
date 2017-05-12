package com.weatherrisk.api.cnst.line.sub.financial;

import com.weatherrisk.api.cnst.line.sub.LineSubFunction;

public enum StockSubFunction implements LineSubFunction {
	UPDATE_STOCK_INFOS("更新股票基本檔"),
	QUERY_MATCH_PRICE("查詢股票目前成交價"),
	HIT_PRICE_INFO("查詢註冊股票到價資訊"),
	QUERY_STOCK_TREASURY("查詢股票庫存")
	;

	private String label;
	
	private StockSubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static StockSubFunction convertByName(String name) {
		for (StockSubFunction e : StockSubFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}

}
