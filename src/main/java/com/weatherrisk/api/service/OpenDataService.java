package com.weatherrisk.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.model.ParkingLotAvailableRepository;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.model.ParkingLotInfoRepository;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotAvailableDetail;
import com.weatherrisk.api.vo.json.tpeopendata.parkinglot.ParkingLotInfoDetail;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeAllInfo;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

public class OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(OpenDataService.class);
	
	private final int RETURN_NEARBY_UBIKE_STATIONS_NUMS = 2;

	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;
	
	@Autowired
	private ParkingLotAvailableRepository parkingLotAvailableRepo;
	
	protected void getNewestParkingLotInfos(boolean isGZipFormat, boolean needToDelete, String parkingLotInfoUrl, JsonDeserializer<ParkingLotInfoDetail> deserializer) {
		try {
			logger.info(">>>>> Prepare to get all parking lot informations from url: <{}>", parkingLotInfoUrl);
			
			long startTime = System.currentTimeMillis();
			
			String jsonData = null;
			if (isGZipFormat) {
				jsonData = HttpUtil.getGzContentFromOpenData(parkingLotInfoUrl);
			}
			else {
				jsonData = HttpUtil.getJsonContentFromOpenData(parkingLotInfoUrl);
			}
			
			logger.info("<<<<< Get all parking lot informations from url: <{}> done, time-spent: <{} ms>", parkingLotInfoUrl, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();
			
			SimpleModule module = new SimpleModule();
			module.addDeserializer(ParkingLotInfoDetail.class, deserializer);
			mapper.registerModule(module);
			
			ParkingLotInfoDetail parkingLotInfoDetail = mapper.readValue(jsonData, ParkingLotInfoDetail.class);
			
			List<ParkingLotInfo> parkingLotInfos = filterDuplicateId(parkingLotInfoDetail.getParkingLotInfos());

			if (needToDelete) {
				logger.info(">>>>> Prepare to delete all parking lot informations...");
				startTime = System.currentTimeMillis();
				parkingLotInfoRepo.deleteAll();
				logger.info("<<<<< Delete all parking lot informations done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
			}
			
			logger.info(">>>>> Prepare to save all parking lot informations, data-size: <{}>...", parkingLotInfos.size());
			startTime = System.currentTimeMillis();
			// insert is more faster than save
			parkingLotInfoRepo.insert(parkingLotInfos);
			logger.info("<<<<< Save all parking lot informations done, data-size: <{}>, time-spent: <{} ms>", parkingLotInfos.size(), System.currentTimeMillis() - startTime);
		} catch (IOException e) {
			logger.error("IOException raised while trying to get newest parking lot informations", e);
		}
	}

	private List<ParkingLotInfo> filterDuplicateId(List<ParkingLotInfo> parkingLotInfos) {
		List<ParkingLotInfo> results = new ArrayList<>();
		
		Set<String> idSets = new HashSet<>();
		
		for (ParkingLotInfo parkingLotInfo : parkingLotInfos) {
			String id = parkingLotInfo.getId();
			if (idSets.contains(id)) {
				continue;
			}
			results.add(parkingLotInfo);
			idSets.add(id);
		}
		
		return results;
	}
	
	protected void getNewestParkingLotAvailable(boolean isGZipFormat, String parkingAvailableUrl, JsonDeserializer<ParkingLotAvailableDetail> deserializer) {
		try {
			logger.info(">>>>> Prepare to get all parking lot availables from url: <{}>", parkingAvailableUrl);
			
			long startTime = System.currentTimeMillis();
			
			String jsonData = null;
			if (isGZipFormat) {
				jsonData = HttpUtil.getGzContentFromOpenData(parkingAvailableUrl);
			}
			else {
				jsonData = HttpUtil.getJsonContentFromOpenData(parkingAvailableUrl);
			}
			
			logger.info("<<<<< Get all parking lot availables from url: <{}> done, time-spent: <{} ms>", parkingAvailableUrl, System.currentTimeMillis() - startTime);
			
			// ref: http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
			ObjectMapper mapper = new ObjectMapper();
			
			SimpleModule module = new SimpleModule();
			module.addDeserializer(ParkingLotAvailableDetail.class, deserializer);
			mapper.registerModule(module);
			
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

	private UBikeAllInfo getNewestUBikeInfo(String ubikeInfoUrl, JsonDeserializer<UBikeAllInfo> deserializer) throws IOException {
		logger.info(">>>>> Prepare to get newest ubike informations from url: <{}>", ubikeInfoUrl);
		
		long startTime = System.currentTimeMillis();
		
		String jsonData = HttpUtil.getJsonContentFromOpenData(ubikeInfoUrl);
		
		logger.info("<<<<< Get newest ubike informations from url: <{}> done, time-spent: <{} ms>", ubikeInfoUrl, System.currentTimeMillis() - startTime);
		
		// ref: http://www.baeldung.com/jackson-deserialization
		ObjectMapper mapper = new ObjectMapper();
		
		SimpleModule module = new SimpleModule();
		module.addDeserializer(UBikeAllInfo.class, deserializer);
		mapper.registerModule(module);

		UBikeAllInfo ubikeAllInfo = mapper.readValue(jsonData, UBikeAllInfo.class);
		
		return ubikeAllInfo;
	}
	
	/**
	 * 從台北市或新北市政府 Open Data 取得 UBike 資料, 並用名稱模糊查詢
	 */
	protected String getNewestUBikeInfoByNameLike(String ubikeInfoUrl, JsonDeserializer<UBikeAllInfo> deserializer, String name) {
		try {
			UBikeAllInfo ubikeAllInfo = getNewestUBikeInfo(ubikeInfoUrl, deserializer);
			
			List<UBikeInfo> ubikeInfos = ubikeAllInfo.getUbikeInfos();
			
			List<UBikeInfo> specificSnaUBikeInfos = ubikeInfos.stream().filter(ubike -> ubike.getSna().contains(name)).collect(Collectors.toList());
			
			if (specificSnaUBikeInfos.isEmpty()) {
				return "請嘗試其他關鍵字";
			}
			
			StringBuilder buffer = new StringBuilder();
			buffer.append(name).append(" UBike 資訊如下:\n");
			buffer.append("---------------------------------\n");
			for (UBikeInfo ubikeInfo : specificSnaUBikeInfos) {
				buffer.append("場站名稱: ").append(ubikeInfo.getSna()).append("\n");
				buffer.append("大概位置: ").append(ubikeInfo.getAr()).append("\n");
				buffer.append("總停車格: ").append(ubikeInfo.getTot()).append("\n");
				buffer.append("可借車輛: ").append(ubikeInfo.getSbi()).append("\n");
				buffer.append("可停空位: ").append(ubikeInfo.getBemp()).append("\n");
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

	/**
	 * 從台北市或新北市政府 Open Data 取得 UBike 資料, 並取出最近的兩個場站
	 */
	protected List<UBikeInfo> getNearbyUBikeStations(String ubikeInfoUrl, JsonDeserializer<UBikeAllInfo> deserializer, Double userLatitude, Double userLongitude) {
		try {
			UBikeAllInfo ubikeAllInfo = getNewestUBikeInfo(ubikeInfoUrl, deserializer);
			
			List<UBikeInfo> ubikeInfos = ubikeAllInfo.getUbikeInfos();
			
			ubikeInfos.stream().forEach(ubikeInfo -> ubikeInfo.setDistance(userLatitude, userLongitude));
			
			Collections.sort(ubikeInfos, new Comparator<UBikeInfo>() {
				@Override
				public int compare(UBikeInfo o1, UBikeInfo o2) {
					return o1.getDistance().compareTo(o2.getDistance());
				}
			});
			
			List<UBikeInfo> results = new ArrayList<>();
			for (int i = 0; i < RETURN_NEARBY_UBIKE_STATIONS_NUMS; i++) {
				results.add(ubikeInfos.get(i));
			}
			
			return results;
			
		} catch (IOException e) {
			logger.error("IOException raised while tring to get newest ubike informations", e);
			return null;
		}
	}
}
