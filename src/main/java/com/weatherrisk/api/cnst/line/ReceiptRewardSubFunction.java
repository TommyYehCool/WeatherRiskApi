package com.weatherrisk.api.cnst.line;

public enum ReceiptRewardSubFunction implements LineSubFunction {
	UPDATE_NUMBERS("抓取最新開獎號碼"),
	GET_LAST_TWO_NUMBERS("查詢最近兩期");
	
	private String label;
	
	private ReceiptRewardSubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static ReceiptRewardSubFunction convertByName(String name) {
		for (ReceiptRewardSubFunction e : ReceiptRewardSubFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
