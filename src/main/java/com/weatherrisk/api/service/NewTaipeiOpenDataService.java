package com.weatherrisk.api.service;

import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.json.deserializer.NewTaipeiUBikeAllInfoDeserializer;

@Service
public class NewTaipeiOpenDataService extends OpenDataService {
	
	private final String UBIKE_INFO_URL = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
	
	/**
	 * 從新台北市政府 Open Data 取得 UBike
	 */
	public String getNewestUBikeInfoByNameLike(String name) {
		return getNewestUBikeInfoByNameLike(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), name);
	}
}
