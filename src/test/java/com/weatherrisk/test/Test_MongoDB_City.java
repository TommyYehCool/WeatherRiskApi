package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.City;
import com.weatherrisk.api.model.CityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_City {

	@Autowired
	private CityRepository cityRepository;
	
	@Test
	public void test_1_deleteAllCities() {
		cityRepository.deleteAll();
		
		System.out.println(">>>>> Test 1: deleteAllCities -> Delete all testing datas done");
	}
	
	@Test
	public void test_2_addCity() throws Exception {
		cityRepository.save(new City(4L, "New Taipei City", "New Taipei City", 123, new Float[] {123F, 456F}));
		
		System.out.println(">>>>> Test 2: addCity -> Add testing datas done");
	}
	
	@Test
	public void test_3_findCityByState() throws Exception {
		String state = "New Taipei City";

		City city = cityRepository.findByState(state);

		assertThat(city).isNotNull();
		assertThat(city.getState()).isEqualTo(state);
		
		System.out.println("Test 3: findCityByState -> " + city);
	}
}
