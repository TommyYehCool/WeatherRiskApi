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
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.model.ParkingLotInfoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_ParkingLotInfo {
	
	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;

	@Test
	public void test_1_findParkingLotInfo() {
		List<ParkingLotInfo> parkingLotInfos = parkingLotInfoRepo.findByArea("中山區");
		for (ParkingLotInfo parkingLotInfo : parkingLotInfos) {
			System.out.println(parkingLotInfo);
		}
	}
}
