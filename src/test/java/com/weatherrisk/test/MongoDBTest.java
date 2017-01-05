package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
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
public class MongoDBTest {
	
	@Autowired
	private CityRepository cityRepository;
	
	@Test
	public void test() throws Exception {
		City city = cityRepository.findByCity("NEW YORK");
		assertThat(city.getCity()).isEqualTo("NEW YORK");
	}
}
