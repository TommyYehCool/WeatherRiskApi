package com.weatherrisk.test;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.cnst.UBikeCity;

public class Test_Java {
	
	@Test
	@Ignore
	public void test_1_testStr() {
		String inputMsg = "#大安森林公園地下停車場";
		String searchStr = inputMsg.substring(1, inputMsg.length());
		System.out.println(searchStr);
	}
	
	@Test
	@Ignore
	public void test_2_message_format() {
		String CWB_URL = "http://opendata.cwb.gov.tw/opendataapi?dataid={0}&authorizationkey={1}";
		
		String dataId = "F-C0032-001";

		String CWB_AUTHOR_API_KEY = "CWB-177B46C0-418B-4126-AC66-99C778E8CABE";
		
		String format = MessageFormat.format(CWB_URL, dataId, CWB_AUTHOR_API_KEY);
		
		System.out.println(format);
	}
	
	@Test
	@Ignore
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
		
		inputMsg = "註冊eth 50 40";
		String cryptoCurrency = inputMsg.substring(inputMsg.indexOf("註冊") + "註冊".length(), inputMsg.length());
		System.out.println(cryptoCurrency);
		
		inputMsg = "取消eth";
		cryptoCurrency = inputMsg.substring(inputMsg.indexOf("取消") + "取消".length(), inputMsg.length());
		System.out.println(cryptoCurrency);
	}
	
	@Test
	@Ignore
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
	@Ignore
	public void test_5_testUBikeCity() {
		String address = "106台灣台北市大安區延吉街70巷5弄10號";
		boolean result = UBikeCity.isSupportedAddress(address);
		System.out.println("test5 --> " + result);
	}
	
	@Test
	@Ignore
	public void test_6_testGMT() {
		String gmtTime = "10:30:00";
		SimpleDateFormat gmtTimeFormat = new SimpleDateFormat("HH:mm:ss");
		gmtTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		SimpleDateFormat gmtAdd8TimeFormat = new SimpleDateFormat("HH:mm:ss");
		gmtAdd8TimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		try {
			Date srcDate = gmtTimeFormat.parse(gmtTime);
			System.out.println(gmtAdd8TimeFormat.format(srcDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void test_7_testSet() {
		Set<String> test = new HashSet<>();
		test.add("123");
		test.add("123");
		test.add("456");
		test.forEach(System.out::println);
	}
	
	@Test
	public void test_8_testDateFormat() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String str = "2017/3/24";
		Date date = dateFormat.parse(str);
		System.out.println(date);
	}
	
	@Test
	public void test_9_testStringUtils() {
		String price = "56.8";
		boolean isNumeric = StringUtils.isNumeric(price);
		System.out.println(isNumeric);
	}
}
