package com.weatherrisk.api.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.util.GetFileUtil;

@Service
public class OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(OpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";

	public void getNewestParkingLotInfos() {
		try {
			String jsonData = GetFileUtil.readGzFromInternet(PARKING_LOT_INFO_URL);
			
			// TODO try to store to MongoDB and construct to object
			System.out.println(jsonData);
		} catch (IOException e) {
			logger.error("IOException raised while tring to get newest parking lot informations", e);
		}
	}
	
	public void getNewestParkingLotAvailable() {
		try {
			String jsonData = GetFileUtil.readGzFromInternet(PARKING_LOT_AVAILABLE_URL);
			
			// TODO try to store to MongoDB and construct to object
			System.out.println(jsonData);
		} catch (IOException e) {
			logger.error("IOException raised while tring to get newest parking lot available", e);
		}
	}
}
