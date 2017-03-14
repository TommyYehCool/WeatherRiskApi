package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.service.BitcoinService;
import com.weatherrisk.api.service.CwbService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_GetDataService {
	
	@Autowired
	private CwbService cwbService;
	
	@Autowired
	private BitcoinService bitcoinService;

	@Test
	@Ignore
	public void test_1_CwbService_getWeatherLittleHelperByCity() {
		String data = cwbService.getWeatherLitteleHelperByCity("台北市");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_2_CwbService_getOneWeekWeatherPrediction() {
		String data = cwbService.getOneWeekWeatherPrediction("臺北市");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_3_CwbService_getOneWeekWeatherPredictionProvided() {
		String data = cwbService.getOneWeekWeatherPredictionProvided();
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_4_BitcoinService_getBitcoinPriceFromWinkdex() {
		String data = bitcoinService.getBitcoinPriceFromWinkdex();
		System.out.println(data);
	}
	
	@Test
	public void test_5_BitcoinService_getPriceFromExchanges() {
		String data = bitcoinService.getPriceFromExchanges(CurrencyPair.BTC_USD);
		System.out.println(data);
		
		data = bitcoinService.getPriceFromExchanges(CurrencyPair.ETH_USD);
		System.out.println(data);
	}
}
