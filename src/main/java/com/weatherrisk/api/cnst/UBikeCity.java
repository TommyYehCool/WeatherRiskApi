package com.weatherrisk.api.cnst;

import java.util.Arrays;
import java.util.List;

public enum UBikeCity {
	TAIPEI(Arrays.asList(new String[] {"台北市", "臺北市"})),
	
	NEW_TAIPEI_CITY(Arrays.asList(new String[] {"新北市"}));
	
	private List<String> cityName;
	
	private UBikeCity(List<String> cityName) {
		this.cityName = cityName;
	}
	
	public List<String> getCityName() {
		return this.cityName;
	}
	
	public static boolean isSupportedCity(String cityName) {
		for (UBikeCity ubikeCity : UBikeCity.values()) {
			if (ubikeCity.cityName.contains(cityName)) {
				return true;
			}
		}
		return false;
	}
	
	public static UBikeCity convert(String cityName) {
		for (UBikeCity ubikeCity : UBikeCity.values()) {
			if (ubikeCity.cityName.contains(cityName)) {
				return ubikeCity;
			}
		}
		return null;
	}
}