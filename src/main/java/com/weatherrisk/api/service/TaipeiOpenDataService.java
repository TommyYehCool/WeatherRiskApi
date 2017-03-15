package com.weatherrisk.api.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeAllInfo;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@Service
public class TaipeiOpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(TaipeiOpenDataService.class);
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	private final String UBIKE_INFO_URL = "http://data.taipei/youbike";
	
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
	
	/**
	 * 從台北市政府 Open Data 取得 UBike
	 */
	public String getNewestUBikeInfoByNameLike(String name) {
		try {
			logger.info(">>>>> Prepare to get newest ubike informations from url: <{}>", UBIKE_INFO_URL);
			
			long startTime = System.currentTimeMillis();
			
			String jsonData = HttpUtil.getJsonContentFromOpenData(UBIKE_INFO_URL);
			
			logger.info("<<<<< Get newest ubike informations from url: <{}> done, time-spent: <{} ms>", UBIKE_INFO_URL, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();

			UBikeAllInfo ubikeAllInfo = mapper.readValue(jsonData, UBikeAllInfo.class);
			
			List<UBikeInfo> ubikeInfos = ubikeAllInfo.getUbikeInfos();
			
			List<UBikeInfo> specificAreaUBikeInfos = ubikeInfos.stream().filter(ubike -> ubike.getSna().contains(name)).collect(Collectors.toList());
			
			if (specificAreaUBikeInfos.isEmpty()) {
				return "請確認您輸入的行政區正確";
			}
			
			StringBuilder buffer = new StringBuilder();
			buffer.append(name).append(" UBike 資訊如下:\n");
			buffer.append("---------------------------------\n");
			for (UBikeInfo ubikeInfo : specificAreaUBikeInfos) {
				buffer.append("場站名稱: ").append(ubikeInfo.getSna()).append("\n");
				buffer.append("大概位置: ").append(ubikeInfo.getAr()).append("\n");
				buffer.append("總停車格: ").append(ubikeInfo.getTot()).append("\n");
				buffer.append("目前車輛數量: ").append(ubikeInfo.getSbi()).append("\n");
				buffer.append("空位數: ").append(ubikeInfo.getBemp()).append("\n");
				buffer.append("緯度: ").append(ubikeInfo.getLat()).append("\n");
				buffer.append("經度: ").append(ubikeInfo.getLng()).append("\n");
				buffer.append("全站禁用狀態: ").append(ubikeInfo.getAct()).append("\n");
				buffer.append("---------------------------------\n");
			}
			
			return buffer.toString();
			
		} catch (IOException e) {
			logger.error("IOException raised while tring to get newest ubike informations", e);
			return "抓取 UBike 資料失敗";
		}
	}
}
