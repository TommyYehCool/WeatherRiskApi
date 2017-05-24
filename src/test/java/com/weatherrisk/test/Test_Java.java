package com.weatherrisk.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

import com.weatherrisk.api.cnst.currency.CurrencyCnst;
import com.weatherrisk.api.cnst.line.main.LineQueryFunction;
import com.weatherrisk.api.cnst.ubike.UBikeCity;

public class Test_Java {
	
	@Test
	@Ignore
	public void test_01_testStr() {
		String inputMsg = "#大安森林公園地下停車場";
		String searchStr = inputMsg.substring(1, inputMsg.length());
		System.out.println(searchStr);
	}
	
	@Test
	@Ignore
	public void test_02_message_format() {
		String CWB_URL = "http://opendata.cwb.gov.tw/opendataapi?dataid={0}&authorizationkey={1}";
		
		String dataId = "F-C0032-001";

		String CWB_AUTHOR_API_KEY = "CWB-177B46C0-418B-4126-AC66-99C778E8CABE";
		
		String format = MessageFormat.format(CWB_URL, dataId, CWB_AUTHOR_API_KEY);
		
		System.out.println(format);
	}
	
	@Test
	@Ignore
	public void test_03_testSubstring() {
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
	public void test_04_testEnumSet() {
		System.out.println(">>>>> Starting to test_04_testEnumSet");
		String code = "Btc";
		boolean result = CurrencyCnst.isCryptoCurrency(code);
		System.out.println(result);
		result = CurrencyCnst.isRealCurrency(code);
		System.out.println(result);
		System.out.println("<<<<< test_04_testEnumSet done");
	}
	
	@Test
	@Ignore
	public void test_05_testUBikeCity() {
		String address = "106台灣台北市大安區延吉街70巷5弄10號";
		boolean result = UBikeCity.isSupportedAddress(address);
		System.out.println("test5 --> " + result);
	}
	
	@Test
	@Ignore
	public void test_06_testGMT() {
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
	public void test_07_testSet() {
		Set<String> test = new HashSet<>();
		test.add("123");
		test.add("123");
		test.add("456");
		test.forEach(System.out::println);
	}
	
	@Test
	@Ignore
	public void test_08_testDateFormat() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String str = "2017/3/24";
		Date date = dateFormat.parse(str);
		System.out.println(date);
	}
	
	@Test
	@Ignore
	public void test_09_testStringUtils() {
		String price = "56.8";
		boolean isNumeric = StringUtils.isNumeric(price);
		System.out.println(isNumeric);
	}
	
	@Test
	@Ignore
	public void test_10_testContain() {
		String inputMsg = "2017/3/24 買 3088 56.8 2000";
		System.out.println(inputMsg.contains("買"));
	}
	
	@Test
	@Ignore
	public void test_11_testBigDecimal() {
		double val = 0.000049;
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		BigDecimal test = new BigDecimal(val);
		System.out.println(decFormat.format(test.doubleValue()));
		System.out.println(test);
		System.out.println(String.valueOf(test));
		System.out.println(test.toPlainString());
		System.out.println(test.toEngineeringString());
	}
	
	@Test
	public void test_12_testBigDecimal() {
		BigDecimal amount = new BigDecimal(0.7124259);
		System.out.println(amount.doubleValue());
		
		BigDecimal totalVolumes = new BigDecimal(19467.7545);
		System.out.println(totalVolumes.doubleValue());
		
		BigDecimal avgPrice = amount.divide(totalVolumes, 8, RoundingMode.CEILING);
		
		DecimalFormat decFormat = new DecimalFormat("0.00000000");
		System.out.println(decFormat.format(avgPrice.doubleValue()));	}
	
	@Test
	@Ignore
	public void test_13_testEnum() {
		LineQueryFunction lineFunction = LineQueryFunction.PARKING_LOT_INFO;
		System.out.println(lineFunction.toString());
	}
	
}
