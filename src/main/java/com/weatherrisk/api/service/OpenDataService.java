package com.weatherrisk.api.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.model.ParkingLogInfoRepository;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.util.GetFileUtil;
import com.weatherrisk.api.vo.json.ParkingLotInfoDetail;

@Service
public class OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(OpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	@Autowired
	private ParkingLogInfoRepository parkingLotInfoRepo;

	public void getNewestParkingLotInfos() {
		try {
			long startTime = System.currentTimeMillis();
			
			logger.info(">>>>> Prepare to get all parking lot informations from url: <{}>", PARKING_LOT_INFO_URL);
			
			String jsonData = GetFileUtil.readGzFromInternet(PARKING_LOT_INFO_URL);
			
			logger.info("<<<<< Get all parking lot informations from url: <{}> done, time-spent: <{} ms>", PARKING_LOT_INFO_URL, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();
			
			ParkingLotInfoDetail parkingLotDetail = mapper.readValue(jsonData, ParkingLotInfoDetail.class);
			
			List<ParkingLotInfo> parkingLotInfos = parkingLotDetail.getParkingLotInfos();
			
			logger.info(">>>>> Prepare to delete all parking lot informations...");

			startTime = System.currentTimeMillis();
			
			parkingLotInfoRepo.deleteAll();
			
			logger.info("<<<<< Delete all parking lot informations done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
			
			logger.info("<<<<< Prepare to save all parking lot informations, data-size: <{}>...", parkingLotInfos.size());

			startTime = System.currentTimeMillis();
			
			// insert is more faster than save
			parkingLotInfoRepo.insert(parkingLotInfos);
			
			logger.info("<<<<< Save all parking lot informations done, data-size: <{}>, time-spent: <{} ms>", parkingLotInfos.size(), System.currentTimeMillis() - startTime);
			
		} catch (IOException e) {
			logger.error("IOException raised while trying to get newest parking lot informations", e);
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
