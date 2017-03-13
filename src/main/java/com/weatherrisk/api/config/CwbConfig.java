package com.weatherrisk.api.config;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CwbConfig {
	
	@Value("${cwb.apikey}")
	private String apiKey;
	
	private String BASE_URL = "http://opendata.cwb.gov.tw/opendataapi?authorizationkey={0}&dataid={1}";
	
	public String getTaiepiWeatherLittleHelperUrl() {
		String dataid = "F-C0032-009";
		return MessageFormat.format(BASE_URL, this.apiKey, dataid);
	}
}
