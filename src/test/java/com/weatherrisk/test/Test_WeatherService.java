package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.service.weather.CwbService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_WeatherService {

	@Autowired
	private CwbService cwbService;
	
	@Test
	public void test_01_getWeatherLittleHelperByCity() {
		String data = cwbService.getWeatherLittleHelperByCity("台北市");
		System.out.println(data);
	}
	
	@Test
	public void test_02_getOneWeekWeatherPrediction() {
		String data = cwbService.getOneWeekWeatherPrediction("臺北市");
		System.out.println(data);
		
		data = cwbService.getOneWeekWeatherPrediction("桃園市");
		System.out.println(data);
	}
}
