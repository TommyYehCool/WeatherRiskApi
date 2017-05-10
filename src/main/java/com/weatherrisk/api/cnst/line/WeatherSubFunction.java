package com.weatherrisk.api.cnst.line;

public enum WeatherSubFunction implements LineSubFunction {
	LITTLE_HELPER("天氣小幫手"),
	ONE_WEEK_PREDICTION("一週天氣");
	
	private String label;
	
	private WeatherSubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static WeatherSubFunction convertByName(String name) {
		for (WeatherSubFunction e : WeatherSubFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
