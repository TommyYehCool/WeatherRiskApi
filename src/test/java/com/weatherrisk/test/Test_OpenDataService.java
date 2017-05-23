package com.weatherrisk.test;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.service.opendata.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.opendata.TaipeiOpenDataService;
import com.weatherrisk.api.service.parkinglot.ParkingLotService;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_OpenDataService {

	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;
	
	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;
	
	@Autowired
	private ParkingLotService parkingLotService;
	
	@Test
	public void test_01_Taipei_getNewestUBikeInfo() {
		String data = taipeiOpenDataService.getNewestUBikeInfoByNameLike("士林");
		System.out.println(data);
	}
	
	@Test
	public void test_02_NewTaipei_getNewestUBikeInfo() {
		String data = newTaipeiOpenDataService.getNewestUBikeInfoByNameLike("三重");
		System.out.println(data);
	}
	
	@Test
	public void test_03_Taipei_getNearbyUBikeStations() {
		double userLatitude = 25.041861;
		double userLongitude = 121.554212;
		List<UBikeInfo> nearbyUBikeStations = taipeiOpenDataService.getNearbyUBikeStations(userLatitude, userLongitude);
		nearbyUBikeStations.stream().forEach(System.out::println);
	}
	
	@Test
	public void test_04_NewTaipei_getNewestParkingLotAvailable() {
		newTaipeiOpenDataService.getNewestParkingLotAvailable();
	}
	
	@Test
	public void test_05_ParkingLotService_findByNameLike() {
		String result = parkingLotService.findByNameLike("市民大道(延吉");
		System.out.println(result);
	}

}
