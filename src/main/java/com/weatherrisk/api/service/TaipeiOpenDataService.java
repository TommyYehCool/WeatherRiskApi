package com.weatherrisk.api.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.model.ParkingLotAvailableRepository;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.model.ParkingLotInfoRepository;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotAvailableDetail;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotInfoDetail;

@Service
public class TaipeiOpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(TaipeiOpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;
	
	@Autowired
	private ParkingLotAvailableRepository parkingLotAvailableRepo;
	
	/**
	 * 從台北市政府 Open Data 取得所有停車場資訊
	 */
	public void getNewestParkingLotInfos() {
		try {
			logger.info(">>>>> Prepare to get all parking lot informations from url: <{}>", PARKING_LOT_INFO_URL);
			
			long startTime = System.currentTimeMillis();
			
			String jsonData = HttpUtil.getGzContentFromOpenData(PARKING_LOT_INFO_URL);
			
			logger.info("<<<<< Get all parking lot informations from url: <{}> done, time-spent: <{} ms>", PARKING_LOT_INFO_URL, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();
			
			ParkingLotInfoDetail parkingLotInfoDetail = mapper.readValue(jsonData, ParkingLotInfoDetail.class);
			
			List<ParkingLotInfo> parkingLotInfos = parkingLotInfoDetail.getParkingLotInfos();
			
			logger.info(">>>>> Prepare to delete all parking lot informations...");

			startTime = System.currentTimeMillis();
			
			parkingLotInfoRepo.deleteAll();
			
			logger.info("<<<<< Delete all parking lot informations done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
			
			logger.info(">>>>> Prepare to save all parking lot informations, data-size: <{}>...", parkingLotInfos.size());

			startTime = System.currentTimeMillis();
			
			// insert is more faster than save
			parkingLotInfoRepo.insert(parkingLotInfos);
			
			logger.info("<<<<< Save all parking lot informations done, data-size: <{}>, time-spent: <{} ms>", parkingLotInfos.size(), System.currentTimeMillis() - startTime);
			
		} catch (IOException e) {
			logger.error("IOException raised while trying to get newest parking lot informations", e);
		}
	}
	
	/**
	 * 從台北市政府 Open Data 取得所有停車場剩餘車位資訊
	 */
	public void getNewestParkingLotAvailable() {
		try {
			logger.info(">>>>> Prepare to get all parking lot availables from url: <{}>", PARKING_LOT_AVAILABLE_URL);
			
			long startTime = System.currentTimeMillis();
			
			String jsonData = HttpUtil.getGzContentFromOpenData(PARKING_LOT_AVAILABLE_URL);
			
			logger.info("<<<<< Get all parking lot availables from url: <{}> done, time-spent: <{} ms>", PARKING_LOT_AVAILABLE_URL, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();
			
			ParkingLotAvailableDetail parkingLotAvailableDetail = mapper.readValue(jsonData, ParkingLotAvailableDetail.class);
			
			List<ParkingLotAvailable> parkingLotAvailables = parkingLotAvailableDetail.getParkingLotAvailables();

			// 這邊不 delete all 了, 改用 save 去處理, 因為資料量遠小於停車場數目
			logger.info(">>>>> Prepare to save all parking lot availables, data-size: <{}>...", parkingLotAvailables.size());
			
			startTime = System.currentTimeMillis();
			
			// 即時車位資訊不一定每次都包含全部, 所以用 save
			parkingLotAvailableRepo.save(parkingLotAvailables);
			
			logger.info("<<<<< Save all parking lot availables done, data-size: <{}>, time-spent: <{} ms>", parkingLotAvailables.size(), System.currentTimeMillis() - startTime);
			
		} catch (IOException e) {
			logger.error("IOException raised while tring to get newest parking lot availables", e);
		}
	}
}
