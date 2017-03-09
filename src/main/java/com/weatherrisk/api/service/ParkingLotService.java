package com.weatherrisk.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.model.ParkingLotAvailableRepository;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.model.ParkingLotInfoRepository;

@Service
public class ParkingLotService {
	
	@Autowired
	private OpenDataService openDataService;

	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;
	
	@Autowired
	private ParkingLotAvailableRepository parkingLotAvailableRepo;
	
	public String findByName(String name) {
		// 取得最新資料
		openDataService.getNewestParkingLotAvailable();
		
		ParkingLotInfo info = findParkingLotInfoByName(name);
		if (info != null) {
			ParkingLotAvailable available = findParkingLotAvailableById(info.getId());
			
			StringBuilder buffer = new StringBuilder();
			
			if (available != null) {
				buffer.append("汽車剩餘位數: ").append(available.getAvailableCar() != -9 ? available.getAvailableCar() : "不提供即時訊息").append("\n")
					  .append("機車剩餘位數: ").append(available.getAvailableMotor() != -9 ? available.getAvailableMotor() : "不提供即時訊息").append("\n");
			}
			buffer.append("行政區: ").append(info.getArea()).append("\n")
				  .append("名稱: ").append(info.getName()).append("\n")
				  .append("簡介: ").append(info.getSummary()).append("\n")
				  .append("地址: ").append(info.getAddress()).append("\n")
				  .append("電話: ").append(info.getTel()).append("\n")
				  .append("收費方式: ").append(info.getPayex()).append("\n")
				  .append("服務時間: ").append(info.getServiceTime()).append("\n")
				  .append("汽車總車位數: ").append(info.getTotalCar()).append("\n")
				  .append("機車總車位數: ").append(info.getTotalMotor()).append("\n")
				  .append("自行車總車位數: ").append(info.getTotalBike()).append("\n");
			return buffer.toString();
		}
		else {
			return "您輸入的停車場名稱, 找不到對應資料";
		}
	}
	
	private List<ParkingLotInfo> findParkingLotInfoByArea(String area) {
		return parkingLotInfoRepo.findByArea(area);
	}
	
	private ParkingLotInfo findParkingLotInfoByName(String name) {
		return parkingLotInfoRepo.findByName(name);
	}

	private ParkingLotAvailable findParkingLotAvailableById(String id) {
		return parkingLotAvailableRepo.findById(id);
	}
}
