package com.weatherrisk.api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.json.deserializer.NewTaipeiParkingLotAvailableDetailDeserializer;
import com.weatherrisk.api.vo.json.deserializer.NewTaipeiParkingLotInfoDetailDeserializer;
import com.weatherrisk.api.vo.json.deserializer.NewTaipeiUBikeAllInfoDeserializer;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@Service
public class NewTaipeiOpenDataService extends OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(NewTaipeiOpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "http://data.ntpc.gov.tw/od/data/api/B1464EF0-9C7C-4A6F-ABF7-6BDF32847E68?$format=json";
	
	private final String PARKING_LOT_AVAILABLE_URL = "http://data.ntpc.gov.tw/od/data/api/E09B35A5-A738-48CC-B0F5-570B67AD9C78?$format=json";
	
	private final String UBIKE_INFO_URL = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
	
	/**
	 * 從新北市政府 Open Data 取得所有停車場資訊
	 */
	public void getNewestParkingLotInfos() {
		boolean isGZipFormat = false;
		boolean needToDelete = false;
		super.getNewestParkingLotInfos(isGZipFormat, needToDelete, PARKING_LOT_INFO_URL, new NewTaipeiParkingLotInfoDetailDeserializer());
	}
	
	/**
	 * 從新北市政府 Open Data 取得所有停車場剩餘車位資訊
	 */
	public void getNewestParkingLotAvailable() {
		boolean isGZipFormat = false;
		super.getNewestParkingLotAvailable(isGZipFormat, PARKING_LOT_AVAILABLE_URL, new NewTaipeiParkingLotAvailableDetailDeserializer());
	}
	
	/**
	 * 從新北市政府 Open Data 查詢對應 UBike 場站資訊 
	 */
	public String getNewestUBikeInfoByNameLike(String name) {
		return super.getNewestUBikeInfoByNameLike(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), name);
	}

	/**
	 * 從新北市政府 Open Data 查詢最近兩個 UBike 場站資訊 
	 */
	public List<UBikeInfo> getNearbyUBikeStations(Double userLatitude, Double userLongitude) {
		logger.info(">>>>> Prepare to get nearby UBike stations at New Taipei City with lat: {}, lng: {}", userLatitude, userLongitude);
		return super.getNearbyUBikeStations(UBIKE_INFO_URL, new NewTaipeiUBikeAllInfoDeserializer(), userLatitude, userLongitude);
	}
}
