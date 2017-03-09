package com.weatherrisk.test;

import org.junit.Test;

public class Test_Java {
	
	@Test
	public void test_1_testStr() {
		String inputMsg = "#大安森林公園地下停車場";
		String searchStr = inputMsg.substring(1, inputMsg.length());
		System.out.println(searchStr);
	}
}
