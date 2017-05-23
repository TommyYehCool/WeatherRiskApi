package com.weatherrisk.test;

import java.math.BigDecimal;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.stock.Stock;
import com.weatherrisk.api.service.stock.StockService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_StockService {

	@Autowired
	private StockService stockService;
	
	@Test
	public void test_01_getStockInfo() {
		stockService.refreshStockInfo();
	}
	
	@Test
	public void test_02_getStockPriceStrByNameOrId() {
		String queryResult = stockService.getStockPriceStrByNameOrId("3088");
		System.out.println(queryResult);
		
		queryResult = stockService.getStockPriceStrByNameOrId("鴻海");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_03_getStockMatchPriceByNameOrId() throws Exception {
		BigDecimal result = stockService.getStockMatchPriceByNameOrId("鴻海");
		System.out.println(result);
	}
	
	@Test
	public void test_04_getStockByNameOrId() {
		Stock stock = stockService.getStockByNameOrId("3088");
		System.out.println(stock);
	}
	
	@Test
	@Ignore
	public void test_05_addBuyStock() {
		String result = stockService.addBuyStock("Tommy", "2017/3/24", "3088", 56.8, 2000);
		System.out.println(result);
	}
	
	@Test
	public void test_06_queryTreasuryStock() {
		String result = stockService.queryTreasuryStock("U8e1ad9783b416aa040e54575e92ef776");
		System.out.println(result);
	}
}
