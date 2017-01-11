package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.Consume;
import com.weatherrisk.api.model.ConsumeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_Consume {
	
	@Autowired
	private ConsumeRepository consumeRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Test
	public void test_1_delelteAllConsumes() throws Exception {
		consumeRepository.deleteAll();
		
		System.out.println(">>>>> Test 1: delelteAllConsumes -> Delete all testing datas done");
	}
	
	@Test
	public void test_2_addConsume() throws Exception {
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
		
		lotteryNo = "87654321";
		prodName = "Lucky Strike";
		amount = 85L;
		consumeRepository.save(new Consume(lotteryNo, user, consumeDate, type, prodName, amount, prize, got, alreadySent));
		
		lotteryNo = "11223344";
		prodName = "御茶園檸檬紅茶";
		amount = 25L;
		consumeRepository.save(new Consume(lotteryNo, user, consumeDate, type, prodName, amount, prize, got, alreadySent));
		
		System.out.println(">>>>> Test 2: addConsume -> Add testing datas done");
	}
	
	@Test
	public void test_3_findConsumeByLotteyNo() throws Exception {
		String lotteryNo = "12345678";

		Consume consume = consumeRepository.findByLotteryNo(lotteryNo);

		assertThat(consume).isNotNull();
		assertThat(consume.getLotteryNo()).isEqualTo(lotteryNo);
		
		System.out.println(">>>>> Test 3: findConsumeByLotteyNo(" + lotteryNo + ") -> " + consume);
	}
	
	/**
	 * 參考: <a href="https://tests4geeks.com/spring-data-boot-mongodb-example/">Spring data boot mongodb example</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_4_findConsumesByExampleLotteryNo() throws Exception {
		String lotteryNo = "12345678";

		List<Consume> consumes 
			= mongoTemplate.find(
					Query.query(
							new Criteria()
								.orOperator(Criteria.where("lotteryNo").regex(lotteryNo, "i"))), 
					Consume.class);

		assertThat(consumes.size()).isEqualTo(1);
		assertThat(consumes.get(0).getLotteryNo()).isEqualTo(lotteryNo);
		
		System.out.println(">>>>> Test 4: findConsumesByExampleLotteryNo(" + lotteryNo + ") -> " + consumes.get(0));
	}

	/**
	 * 參考: <a href="http://www.baeldung.com/queries-in-spring-data-mongodb">queries-in-spring-data-mongodb</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_5_findConsumesByExampleProdName() throws Exception {
		String prodName = "Lucky Strike";
		
		Query query = new Query();
		query.addCriteria(Criteria.where("prodName").is(prodName));

		List<Consume> consumes = mongoTemplate.find(query, Consume.class);

		assertThat(consumes.size()).isEqualTo(1);
		assertThat(consumes.get(0).getProdName()).isEqualTo(prodName);
		
		System.out.println(">>>>> Test 5: findConsumesByExampleProdName(" + prodName + ") -> " + consumes.get(0));
	}
	
	/**
	 * 參考: <a href="http://www.baeldung.com/queries-in-spring-data-mongodb">queries-in-spring-data-mongodb</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_6_findConsumesByProdNameStartingWith() throws Exception {
		String prodNameStartingWith = "Lucky";
		String prodName = "Lucky Strike";
		
		List<Consume> consumes = consumeRepository.findByProdNameStartingWith(prodNameStartingWith);
		
		assertThat(consumes.size()).isEqualTo(1);
		assertThat(consumes.get(0).getProdName()).startsWith(prodNameStartingWith);
		assertThat(consumes.get(0).getProdName()).isEqualTo(prodName);
		
		System.out.println(">>>>> Test 6: findConsumesByProdNameStartingWith(" + prodNameStartingWith + ") -> " + consumes.get(0));
	}
	
	/**
	 * 參考: <a href="http://www.baeldung.com/queries-in-spring-data-mongodb">queries-in-spring-data-mongodb</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_7_findConsumesByAmountBetween() throws Exception {
		Long amountGT = 10L;
		Long amountLT = 30L;
		
		List<Consume> consumes = consumeRepository.findByAmountBetween(amountGT, amountLT);
		
		assertThat(consumes.size()).isEqualTo(2);
		
		System.out.println(">>>>> Test 7: findConsumesByAmountBetween(" + amountGT + "," + amountLT + ") -> " + consumes);
	}
	
	/**
	 * 參考: <a href="http://www.baeldung.com/queries-in-spring-data-mongodb">queries-in-spring-data-mongodb</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_8_findConsumesByProdNameLikeOrderByLotteryNo() throws Exception {
		String prodNameLike = "御";
		
		List<Consume> consumes = consumeRepository.findByProdNameLikeOrderByLotteryNoAsc(prodNameLike);
		
		assertThat(consumes.size()).isEqualTo(2);
		
		for (Consume consume : consumes) {
			assertThat(consume.getProdName()).contains(prodNameLike);
		}
		 
		System.out.println(">>>>> Test 8: findConsumesByProdNameLikeOrderByLotteryNo(" + prodNameLike + ") -> " + consumes);
	}
}
