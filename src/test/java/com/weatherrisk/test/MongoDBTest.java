package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.Activity;
import com.weatherrisk.api.model.ActivityRepository;
import com.weatherrisk.api.model.City;
import com.weatherrisk.api.model.CityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
public class MongoDBTest {
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private CityRepository cityRepository;
	
	@Test
	public void testFindActivityById() {
		long id = 1L;
		Activity activity = activityRepository.findById(id);
		System.out.println(activity);
		assertThat(activity).isNotNull();
		assertThat(activity.getId()).isEqualTo(id);
	}
	
	@Test
	public void testFindActivityByCreateUser() {
		String createUser = "Tommy";
		Activity activity = activityRepository.findByCreateUser(createUser);
		System.out.println(activity);
		assertThat(activity).isNotNull();
		assertThat(activity.getCreateUser()).isEqualTo(createUser);
	}
	
	@Test
	public void testAddCity() throws Exception {
		cityRepository.save(new City(4L, "New Taipei City", "New Taipei City", 123, new Float[] {123F, 456F}));
	}
	
	@Test
	public void testFindCityByState() throws Exception {
		String state = "NY";
		City city = cityRepository.findByState(state);
		System.out.println(city);
		assertThat(city).isNotNull();
		assertThat(city.getState()).isEqualTo(state);
	}
}
