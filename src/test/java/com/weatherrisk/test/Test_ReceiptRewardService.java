package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.service.receiptreward.ReceiptRewardService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_ReceiptRewardService {

	@Autowired
	private ReceiptRewardService receiptRewardService;
	
	@Test
	public void test_01_getRecentlyRewards() {
		String queryResult = receiptRewardService.getRecentlyRewards();
		System.out.println(queryResult);
	}

	@Test
	public void test_02_checkIsBingo() {
		String queryResult = receiptRewardService.checkIsBingo("478");
		System.out.println(queryResult);
	}
}
