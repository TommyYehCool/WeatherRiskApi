package com.weatherrisk.api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.json.deserializer.TaipeiParkingLotAvailableDetailDeserializer;
import com.weatherrisk.api.vo.json.deserializer.TaipeiParkingLotInfoDetailDeserializer;
import com.weatherrisk.api.vo.json.deserializer.TaipeiUBikeAllInfoDeserializer;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@Service
public class TaipeiOpenDataService extends OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(TaipeiOpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	private final String UBIKE_INFO_URL = "http://data.taipei/youbike";
	
	/**
	 * 從台北市政府 Open Data 取得所有停車場資訊
	 */
	public void getNewestParkingLotInfos() {
		boolean isGZipFormat = true;
		boolean needToDelete = true;
		super.getNewestParkingLotInfos(isGZipFormat, needToDelete, PARKING_LOT_INFO_URL, new TaipeiParkingLotInfoDetailDeserializer());
	}
	
	/**
	 * 從台北市政府 Open Data 取得所有停車場剩餘車位資訊
	 */
	public void getNewestParkingLotAvailable() {
		boolean isGZipFormat = true;
		super.getNewestParkingLotAvailable(isGZipFormat, PARKING_LOT_AVAILABLE_URL, new TaipeiParkingLotAvailableDetailDeserializer());
	}
	
	public String getNewestUBikeInfoByNameLike(String name) {
		return super.getNewestUBikeInfoByNameLike(UBIKE_INFO_URL, new TaipeiUBikeAllInfoDeserializer(), name);
	}

	public List<UBikeInfo> getNearbyUBikeStations(Double userLatitude, Double userLongitude) {
		logger.info(">>>>> Prepare to get nearby UBike stations at Taipei with lat: {}, lng: {}", userLatitude, userLongitude);
		return super.getNearbyUBikeStations(UBIKE_INFO_URL, new TaipeiUBikeAllInfoDeserializer(), userLatitude, userLongitude);
	}
}
