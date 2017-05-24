package com.weatherrisk.test;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.cnst.currency.CurrencyCnst;
import com.weatherrisk.api.service.currency.CurrencyService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_CryptoCurrencyService {
	
	@Autowired
	private CurrencyService currencyService;
	
	@Test
	public void test_01_getBitcoinPriceFromWinkdex() {
		String data = currencyService.getBitcoinPriceFromWinkdex();
		System.out.println(data);
	}
	
	@Test
	public void test_02_getPriceFromExchanges() {
		String data = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.BTC_USD);
		System.out.println(data);
		
		data = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.ETH_USD);
		System.out.println(data);
		
		data = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.STR_BTC);
		System.out.println(data);
		
		data = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.XRP_BTC);
		System.out.println(data);
	}
	
	@Test
	public void test_03_getRatesFromTaiwanBank() throws IOException {
		String data = currencyService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.USD);
		System.out.println(data);
		
		data = currencyService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.JPY);
		System.out.println(data);
	}
	
	@Test
	public void test_04_queryTreasuryCryptoCurrency() {
		String result = currencyService.queryTreasuryCryptoCurrency("U8e1ad9783b416aa040e54575e92ef776");
		System.out.println(result);
	}
}
