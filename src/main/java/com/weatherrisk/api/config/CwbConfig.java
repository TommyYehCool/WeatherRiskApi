package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CwbConfig {
	
	@Value("${cwb.apikey}")
	private String apiKey;
	
	private String BASE_URL = "http://opendata.cwb.gov.tw/opendataapi?authorizationkey={0}&dataid={1}";
	
	public String getApiKey() {
		return this.apiKey;
	}
	
	public String getNormalWeatherPredictionUrl() {
		String dataid = "F-C0032-001";
		return MessageFormat.format(BASE_URL, getApiKey(), dataid);
	}
}
