package com.weatherrisk.test;

import java.util.Arrays;
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
import com.weatherrisk.api.model.parkinglot.ParkingLotAvailable;
import com.weatherrisk.api.model.parkinglot.ParkingLotAvailableRepository;
import com.weatherrisk.api.model.parkinglot.ParkingLotInfo;
import com.weatherrisk.api.model.parkinglot.ParkingLotInfoRepository;
import com.weatherrisk.api.service.parkinglot.ParkingLotService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_ParkingLot {
	
	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;
	
	@Autowired
	private ParkingLotAvailableRepository parkingLotAvailableRepo;
	
	@Autowired
	private ParkingLotService parkingLotService;

	@Test
	public void test_01_findParkingLotInfo_ByArea() {
		String area = "中山區";
		System.out.println(">>>>> Test find by area: <" + area + ">");
		List<ParkingLotInfo> parkingLotInfos = parkingLotInfoRepo.findByArea(area);
		for (ParkingLotInfo parkingLotInfo : parkingLotInfos) {
			System.out.println(parkingLotInfo);
		}
	}
	
	@Test
	public void test_02_findParkingLotInfo_ByNameLike() {
		String name = "市民";
		System.out.println(">>>>> Test find by name like: <" + name + ">");
		List<ParkingLotInfo> parkingLotInfos = parkingLotInfoRepo.findByNameLike(name);
		for (ParkingLotInfo parkingLotInfo : parkingLotInfos) {
			System.out.println(parkingLotInfo);
		}
	}
	
	@Test
	public void test_03_findParkingLotAvailable_ById() {
		String id = "056";
		System.out.println(">>>>> Test find by id: <" + id + ">");
		ParkingLotAvailable parkingLotAvailable = parkingLotAvailableRepo.findById(id);
		System.out.println(parkingLotAvailable);
	}
	
	@Test
	public void test_04_findParkingLotAvailable_ByIdIn() {
		List<String> ids = Arrays.asList(new String[] {"058", "057"});
		System.out.println(">>>>> Test find by id in: <" + ids + ">");
		List<ParkingLotAvailable> parkingLotAvailables = parkingLotAvailableRepo.findByIdIn(ids);
		for (ParkingLotAvailable parkingLotAvailable : parkingLotAvailables) {
			System.out.println(parkingLotAvailable);
		}
	}
	
	@Test
	public void test_05_testParkingLotService() {
		parkingLotService.findByNameLike("市民");
	}
}
