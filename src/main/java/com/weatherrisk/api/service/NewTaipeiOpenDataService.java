package com.weatherrisk.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.json.deserializer.NewTaipeiUBikeAllInfoDeserializer;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@Service
public class NewTaipeiOpenDataService extends OpenDataService {
	
	private final String UBIKE_INFO_URL = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
	
	public String getNewestUBikeInfoByNameLike(String name) {
		return super.getNewestUBikeInfoByNameLike(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), name);
	}

	public List<UBikeInfo> getNearbyUBikeStations(Double latitude, Double longitude) {
		return super.getNearbyUBikeStations(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), latitude, longitude);
	}
}
