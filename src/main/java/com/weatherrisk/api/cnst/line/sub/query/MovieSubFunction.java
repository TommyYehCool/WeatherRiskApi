package com.weatherrisk.api.cnst.line.sub.query;

import com.weatherrisk.api.cnst.line.sub.LineSubFunction;

public enum MovieSubFunction implements LineSubFunction {
	UPDATE_MOVIE_TIME("更新電影時刻表"),
	QUERY_MOVIE_TIME("查詢電影時刻表")
	;

	private String label;
	
	private MovieSubFunction(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public static MovieSubFunction convertByName(String name) {
		for (MovieSubFunction e : MovieSubFunction.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
