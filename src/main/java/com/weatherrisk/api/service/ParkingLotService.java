package com.weatherrisk.api.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.model.ParkingLotAvailable;
import com.weatherrisk.api.model.ParkingLotAvailableRepository;
import com.weatherrisk.api.model.ParkingLotInfo;
import com.weatherrisk.api.model.ParkingLotInfoRepository;

@Service
public class ParkingLotService {
	
	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;
	
	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;

	@Autowired
	private ParkingLotInfoRepository parkingLotInfoRepo;
	
	@Autowired
	private ParkingLotAvailableRepository parkingLotAvailableRepo;
	
	/**
	 * 更新及時車位資訊
	 */
	private void refreshNewestParkingLotAvailable() {
		taipeiOpenDataService.getNewestParkingLotAvailable();
		newTaipeiOpenDataService.getNewestParkingLotAvailable();
	}
	
	public String findByName(String name) {
		refreshNewestParkingLotAvailable();
		
		ParkingLotInfo info = parkingLotInfoRepo.findByName(name);
		if (info != null) {
			ParkingLotAvailable available = parkingLotAvailableRepo.findById(info.getId());
			
			StringBuilder buffer = new StringBuilder();
			
			if (available != null) {
				buffer.append("汽車剩餘位數: ").append(available.getAvailableCar() != -9 ? available.getAvailableCar() : "不提供即時訊息").append("\n")
					  .append("機車剩餘位數: ").append(available.getAvailableMotor() != -9 ? available.getAvailableMotor() : "不提供即時訊息").append("\n")
					  .append("\n");
			}
			buffer.append("行政區：").append(info.getArea()).append("\n")
				  .append("名稱：").append(info.getName()).append("\n")
				  .append("簡介：").append(info.getSummary()).append("\n")
				  .append("地址：").append(info.getAddress()).append("\n")
				  .append("電話：").append(info.getTel()).append("\n")
				  .append("收費方式：").append(info.getPayex()).append("\n")
				  .append("服務時間：").append(info.getServiceTime()).append("\n")
				  .append("汽車總車位數：").append(info.getTotalCar()).append("\n")
				  .append("機車總車位數：").append(info.getTotalMotor()).append("\n")
				  .append("自行車總車位數：").append(info.getTotalBike()).append("\n");
			return buffer.toString();
		}
		else {
			return "您輸入的停車場名稱, 找不到對應資料";
		}
	}
	
	public String findByNameLike(String name) {
		refreshNewestParkingLotAvailable();
		
		List<ParkingLotInfo> infos = parkingLotInfoRepo.findByNameLike(name);
		
		if (infos != null && !infos.isEmpty()) {
			// http://stackoverflow.com/questions/737244/java-map-a-list-of-objects-to-a-list-with-values-of-their-property-attributes
			List<String> ids = infos.stream().map(ParkingLotInfo::getId).collect(Collectors.toList());

			List<ParkingLotAvailable> availables = parkingLotAvailableRepo.findByIdIn(ids);
			
			// http://stackoverflow.com/questions/20363719/java-8-listv-into-mapk-v
			Map<String, ParkingLotAvailable> map = availables.stream().collect(Collectors.toMap(ParkingLotAvailable::getId, Function.identity()));
			
			StringBuilder buffer = new StringBuilder();
			for (ParkingLotInfo info : infos) {
				buffer.append("名稱：").append(info.getName()).append("\n")
					  .append("地址：").append(info.getAddress()).append("\n")
					  .append("服務時間：").append(info.getServiceTime()).append("\n");
				ParkingLotAvailable available = map.get(info.getId());
				if (available != null) {
					buffer.append("汽車剩餘位數: ").append(available.getAvailableCar() != -9 ? available.getAvailableCar() : "不提供即時訊息").append("\n")
					  	  .append("機車剩餘位數: ").append(available.getAvailableMotor() != -9 ? available.getAvailableMotor() : "不提供即時訊息").append("\n");
				}
				buffer.append("-------------\n");
			}
			return buffer.toString();
		}
		else {
			return "您輸入的停車場名稱, 找不到對應資料";
		}
	}
	
}
