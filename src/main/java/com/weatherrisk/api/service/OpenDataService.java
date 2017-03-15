package com.weatherrisk.api.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeAllInfo;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

public class OpenDataService {
	
	private Logger logger = LoggerFactory.getLogger(OpenDataService.class);
	
	/**
	 * 從台北市政府 Open Data 取得 UBike
	 */
	public String getNewestUBikeInfoByNameLike(String ubikeInfoUrl, JsonDeserializer<UBikeAllInfo> deserializer, String name) {
		try {
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
			
			List<UBikeInfo> ubikeInfos = ubikeAllInfo.getUbikeInfos();
			
			List<UBikeInfo> specificAreaUBikeInfos = ubikeInfos.stream().filter(ubike -> ubike.getSna().contains(name)).collect(Collectors.toList());
			
			if (specificAreaUBikeInfos.isEmpty()) {
				return "請嘗試其他關鍵字";
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
