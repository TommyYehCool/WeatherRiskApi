package com.weatherrisk.api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.json.deserializer.NewTaipeiUBikeAllInfoDeserializer;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@Service
public class NewTaipeiOpenDataService extends OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(NewTaipeiOpenDataService.class);
	
	private final String UBIKE_INFO_URL = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
	
	public String getNewestUBikeInfoByNameLike(String name) {
		return super.getNewestUBikeInfoByNameLike(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), name);
	}

	public List<UBikeInfo> getNearbyUBikeStations(Double userLatitude, Double userLongitude) {
		logger.info(">>>>> Prepare to get nearby UBike stations at New Taipei City with lat: {}, lng: {}", userLatitude, userLongitude);
		return super.getNearbyUBikeStations(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), userLatitude, userLongitude);
	}
}
