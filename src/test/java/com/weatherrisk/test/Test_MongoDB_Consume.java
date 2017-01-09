package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.Consume;
import com.weatherrisk.api.model.ConsumeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.DEFAULT)
public class Test_MongoDB_Consume {
	
	@Autowired
	private ConsumeRepository consumeRepository;
	
	@Test
	public void test_1_addConsume() throws Exception {
		String lotteryNo = "12345678";
		String user = "Tommy";
		Date consumeDate = new Date();
		Integer type = 1;
		String prodName = "御茶園";
		Long amount = 23L;
		Long prize = 0L;
		Boolean got = false;
		Boolean alreadySent = false;
		consumeRepository.save(new Consume(lotteryNo, user, consumeDate, type, prodName, amount, prize, got, alreadySent));
	}
	
	@Test
	public void test_2_findConsumeByLotteyNo() throws Exception {
		String lotteryNo = "12345678";
		Consume consume = consumeRepository.findByLotteryNo(lotteryNo);
		System.out.println(consume);
		assertThat(consume).isNotNull();
		assertThat(consume.getLotteryNo()).isEqualTo(lotteryNo);
	}
}
