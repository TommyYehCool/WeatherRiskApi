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
import com.weatherrisk.api.model.Attraction;
import com.weatherrisk.api.model.AttractionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_Attraction {

	@Autowired
	private AttractionRepository attractionRepository;
	
	@Test
	public void test_1_deleteAllAttractions() {
		attractionRepository.deleteAll();
		
		System.out.println(">>>>> Test 1: deleteAllAttractions -> Delete all testing datas done");
	}
	
	@Test
	public void test_2_addAttractions() throws Exception {
		Long id = 1L;
		String country = "Taiwan";
		String name = "偷米家";
		Float[] loc = new Float[] {25.076198F, 121.480525F};
		attractionRepository.save(new Attraction(id, country, name, loc));
		
		id = 2L;
		name = "白白家";
		loc = new Float[] {25.005747F, 121.465384F};
		attractionRepository.save(new Attraction(id, country, name, loc));
		
		System.out.println(">>>>> Test 2: addAttractions -> Add testing datas done");
	}
	
}
