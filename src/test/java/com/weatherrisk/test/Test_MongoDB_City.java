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
@FixMethodOrder(MethodSorters.DEFAULT)
public class Test_MongoDB_City {

	@Autowired
	private CityRepository cityRepository;
	
	@Test
	public void test_1_AddCity() throws Exception {
		cityRepository.save(new City(4L, "New Taipei City", "New Taipei City", 123, new Float[] {123F, 456F}));
	}
	
	@Test
	public void test_2_FindCityByState() throws Exception {
		String state = "New Taipei City";
		City city = cityRepository.findByState(state);
		System.out.println(city);
		assertThat(city).isNotNull();
		assertThat(city.getState()).isEqualTo(state);
	}
	
	@Test
	public void test_3_DeleteAllCities() {
		cityRepository.deleteAll();
	}
}
