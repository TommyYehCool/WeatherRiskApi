package com.weatherrisk.api.config;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CwbConfig {
	
	private final String BASE_URL = "http://opendata.cwb.gov.tw/opendataapi?authorizationkey={0}&dataid={1}";
	
	private String oneWeekWeatherPredictionUrl; 
	
	private String taoyuanOneWeekWeatherPredictionUrl;
	
	private Map<String, String> cityWeatherLittleHelpUrl = new HashMap<>();
	
	@Autowired
	public CwbConfig(@Value("${cwb.apikey}") String apiKey) {
		oneWeekWeatherPredictionUrl = MessageFormat.format(BASE_URL, apiKey, "F-C0032-003");
		
		taoyuanOneWeekWeatherPredictionUrl = MessageFormat.format(BASE_URL, apiKey, "F-D0047-007");
		
		cityWeatherLittleHelpUrl.put("臺北市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-009"));
		cityWeatherLittleHelpUrl.put("新北市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-010"));
		cityWeatherLittleHelpUrl.put("基隆市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-011"));
		cityWeatherLittleHelpUrl.put("花蓮縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-012"));
		cityWeatherLittleHelpUrl.put("宜蘭縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-013"));
		cityWeatherLittleHelpUrl.put("金門縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-014"));
		cityWeatherLittleHelpUrl.put("澎湖縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-015"));
		cityWeatherLittleHelpUrl.put("台南市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-016"));
		cityWeatherLittleHelpUrl.put("高雄市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-017"));
		cityWeatherLittleHelpUrl.put("嘉義縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-018"));
		cityWeatherLittleHelpUrl.put("嘉義市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-019"));
		cityWeatherLittleHelpUrl.put("苗栗縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-020"));
		cityWeatherLittleHelpUrl.put("台中市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-021"));
		cityWeatherLittleHelpUrl.put("桃園市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-022"));
		cityWeatherLittleHelpUrl.put("新竹縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-023"));
		cityWeatherLittleHelpUrl.put("新竹市", MessageFormat.format(BASE_URL, apiKey, "F-C0032-024"));
		cityWeatherLittleHelpUrl.put("屏東縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-025"));
		cityWeatherLittleHelpUrl.put("南投縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-026"));
		cityWeatherLittleHelpUrl.put("台東縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-027"));
		cityWeatherLittleHelpUrl.put("彰化縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-028"));
	    cityWeatherLittleHelpUrl.put("雲林縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-029"));
        cityWeatherLittleHelpUrl.put("連江縣", MessageFormat.format(BASE_URL, apiKey, "F-C0032-030"));
	}
	
	public String getOneWeekWeatherPredictionUrl() {
		return oneWeekWeatherPredictionUrl;
	}
	
	public String getTaoyuanOneWeekWeatherPredictionUrl() {
		return taoyuanOneWeekWeatherPredictionUrl;
	}
     
	public String getLittleHelperUrlByCity(String city) {
		return cityWeatherLittleHelpUrl.get(city);
	}
	
}
