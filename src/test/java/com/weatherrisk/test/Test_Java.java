package com.weatherrisk.test;

import java.text.MessageFormat;

import org.junit.Test;

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
	}
}
