package com.weatherrisk.test;

import java.text.MessageFormat;

import org.junit.Test;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.cnst.UBikeCity;

public class Test_Java {
	
	@Test
	public void test_1_testStr() {
		String inputMsg = "#大安森林公園地下停車場";
		String searchStr = inputMsg.substring(1, inputMsg.length());
		System.out.println(searchStr);
	}
	
	@Test
	public void test_2_message_format() {
		String CWB_URL = "http://opendata.cwb.gov.tw/opendataapi?dataid={0}&authorizationkey={1}";
		
		String dataId = "F-C0032-001";

		String CWB_AUTHOR_API_KEY = "CWB-177B46C0-418B-4126-AC66-99C778E8CABE";
		
		String format = MessageFormat.format(CWB_URL, dataId, CWB_AUTHOR_API_KEY);
		
		System.out.println(format);
	}
	
	@Test
	public void test_3_testSubstring() {
		String inputMsg = "台北市天氣";
		String city = inputMsg.substring(0, inputMsg.length() - 2);
		System.out.println(city);
		
		inputMsg = "桃園市中壢區一周";
		String taoyuanRegion = inputMsg.substring(3, inputMsg.length() - 2);
		System.out.println(taoyuanRegion);
		
		inputMsg = "台北市士林ubike";
		String name = inputMsg.substring(inputMsg.indexOf("台北市") + "台北市".length(), inputMsg.indexOf("ubike"));
		System.out.println(name);
		
		inputMsg = "信義威秀金剛";
		String chineseName = "信義威秀";
		String filmName = inputMsg.substring(chineseName.length(), inputMsg.length());
		System.out.println(filmName);
	}
	
	@Test
	public void test_4_testEnumSet() {
		System.out.println(">>>>> Starting to test_4_testEnumSet");
		String code = "Btc";
		boolean result = CurrencyCnst.isCryptoCurrency(code);
		System.out.println(result);
		result = CurrencyCnst.isRealCurrency(code);
		System.out.println(result);
		System.out.println("<<<<< test_4_testEnumSet done");
	}
	
	@Test
	public void test_5_testUBikeCity() {
		String address = "106台灣台北市大安區延吉街70巷5弄10號";
		boolean result = UBikeCity.isSupportedAddress(address);
		System.out.println("test5 --> " + result);
	}
}
