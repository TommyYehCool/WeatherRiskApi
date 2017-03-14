package com.weatherrisk.api.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.config.CwbConfig;
import com.weatherrisk.api.vo.xml.cwb.CwbOpenData;
import com.weatherrisk.api.vo.xml.cwb.Dataset;
import com.weatherrisk.api.vo.xml.cwb.Location;
import com.weatherrisk.api.vo.xml.cwb.Parameter;
import com.weatherrisk.api.vo.xml.cwb.ParameterSet;
import com.weatherrisk.api.vo.xml.cwb.Time;
import com.weatherrisk.api.vo.xml.cwb.WeatherElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class CwbService {
	
	private Logger logger = LoggerFactory.getLogger(CwbService.class);

	@Autowired
	private CwbConfig cwbConfig;
	
	public String getOneWeekWeatherPrediction(String region) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CwbOpenData.class);
			
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			String url = cwbConfig.getOneWeekWeatherPredictionUrl();
			
			logger.info("----> Prepare to get one week weather prediction from url: <{}>", url);
			
			CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(new URL(url));
			
			Dataset dataset = data.getDataset();
			if (dataset == null) {
				return "不好意思, 程式寫太爛, 壞掉啦!";
			}
			
			List<Location> locations = dataset.getLocation();
			
			List<Location> regionLoc = locations.stream().filter(l -> l.getLocationName().equals(region)).collect(Collectors.toList());
			if (regionLoc.isEmpty() || regionLoc.size() != 1) {
				return "找不到對應一週資訊, 請確認輸入為 'xxxx一週";
			}
			
			// 用來暫存資訊
			Map<String, OneDayWeather> tempMap = new LinkedHashMap<>();
			
			// 用來組出回傳訊息
			StringBuilder buffer = new StringBuilder();

			Location location = regionLoc.get(0);
			
			buffer.append(location.getLocationName()).append(" 一週天氣資訊:\n");
			
			List<WeatherElement> weatherElements = location.getWeatherElement();
			
			for (WeatherElement weatherElement : weatherElements) {
				OneDayWeather oneDayWeather = null;
				
				String elementName = weatherElement.getElementName();
				List<Time> times = null;
				switch (elementName) {
					case "Wx":
						times = weatherElement.getTime();
						for (Time time : times) {
							String startTime = time.getStartTime();
							startTime = startTime.substring(0, startTime.indexOf("T"));

							String desc = time.getParameter().getParameterName();
							
							oneDayWeather = new OneDayWeather();
							oneDayWeather.setTime(startTime);
							oneDayWeather.setDesc(desc);
							
							tempMap.put(startTime, oneDayWeather);
						}
						break;
						
					case "MaxT":
					case "MinT":
						times = weatherElement.getTime();
						for (Time time : times) {
							String startTime = time.getStartTime();
							startTime = startTime.substring(0, startTime.indexOf("T"));
							
							Parameter parameter = time.getParameter();
							String temperatureVal = parameter.getParameterName();
							String temperatureUnit = parameter.getParameterUnit();
							
							oneDayWeather = tempMap.get(startTime);
							
							if (elementName.equals("MaxT")) {
								oneDayWeather.setMaxT(temperatureVal + temperatureUnit);
							}
							else {
								oneDayWeather.setMinT(temperatureVal + temperatureUnit);
							}
						}
						break;
				}
			}
			
			// ------ 從 Map 中取出整理好的資訊, 組出要回傳的訊息 ------
			Iterator<String> keys = tempMap.keySet().iterator();
			while (keys.hasNext()) {
				String time = keys.next();
				OneDayWeather oneDayWeather = tempMap.get(time);
				
				buffer.append(oneDayWeather.getTime()).append("     ");
				buffer.append(oneDayWeather.getMinT()).append(" ~ ");
				buffer.append(oneDayWeather.getMaxT()).append("     ");
				buffer.append(oneDayWeather.getDesc()).append("\n");
				
			}
			
			return buffer.toString();
			
		} catch (IOException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		} catch (JAXBException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		} 
	}
	
	@Data
	@NoArgsConstructor
	private class OneDayWeather {
		private String time;
		private String desc;
		private String maxT;
		private String minT;
	}
	
	public String getWeatherLitteleHelperByCity(String city) {
		try {
			city = checkCityName(city);
			
//			JAXBContext jaxbContext 
//				= JAXBContext.newInstance(
//						new Class[] {
//							CwbOpenData.class, Dataset.class, DatasetInfo.class, Location.class, ParameterSet.class, Parameter.class
//						}
//				  );
			JAXBContext jaxbContext = JAXBContext.newInstance(CwbOpenData.class);
			
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			String url = cwbConfig.getLittleHelperUrlByCity(city);
			if (url == null) {
				return "請輸入正確城市名稱";
			}
			
			long startTime = System.currentTimeMillis();
			
			logger.info("----> Prepare to get weather little helper with city: <{}> from url: <{}>", city, url);
			
			// 這種寫法本機跑沒問題, 但放到  Heroku 會壞掉, maybe is the JDK version problem, heroku use OpenJDK
			// 幹真的是 JDK 版本問題!!!!!!!!!!!!!!!!!!
			// https://devcenter.heroku.com/articles/java-support
			// https://bugs.openjdk.java.net/browse/JDK-8165299
			CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(new URL(url));

			// 直接索取 xml content 並解析方法
//			String xmlContent = HttpUtil.getWeatherContentFromCwb(url);
//			logger.info("<---- Got response, xml content: {}", xmlContent);
//
//			StringReader reader = new StringReader(xmlContent);
//			CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(reader);
			
			logger.info("<----- Unmarshal to CwbOpenData done, time-spent: {} ms, result: {}", System.currentTimeMillis() - startTime, data);
			
			Dataset dataset = data.getDataset();
			if (dataset == null) {
				return "不好意思, 程式寫太爛, 壞掉啦!";
			}
			
			ParameterSet parameterSet = dataset.getParameterSet();

			StringBuilder buffer = new StringBuilder();
			buffer.append(dataset.getLocation().get(0).getLocationName()).append("-");
			
			buffer.append(parameterSet.getParameterSetName()).append("\n\n");
			
			List<Parameter> parameters = parameterSet.getParameter();
			for (Parameter parameter : parameters) {
				buffer.append(parameter.getParameterValue()).append("\n\n");
			}
			
			return buffer.toString();

		} catch (IOException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		} catch (JAXBException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		}
	}

	private String checkCityName(String city) {
		if (city.contains("台")) {
			city = city.replaceAll("台", "臺");
		}
		return city;
	}
}
