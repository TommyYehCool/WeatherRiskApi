package com.weatherrisk.test;

import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.Test;

import com.weatherrisk.api.cnst.stock.StockType;
import com.weatherrisk.api.model.stock.TreasuryStock;

public class Test_TreasuryStock {
	
	@Test
	public void test() throws ParseException {
		double dFeePercent = 0.001425;
		BigDecimal feePercent = new BigDecimal(dFeePercent);
		System.out.println(feePercent.doubleValue());
		
		TreasuryStock stock = new TreasuryStock(); 
		stock.setStockType(StockType.OTC);
		stock.setStockId("3088"); 
		stock.setStockName("艾訊");
		stock.setBuyDate("2017/3/24");
		stock.setBuyPriceAndShares(56.8d, 2000);
		stock.setSellDate("2017/4/12"); 
		stock.setSellPriceAndShares(60.9d, 2000);

		System.out.println(stock);
	}
}
