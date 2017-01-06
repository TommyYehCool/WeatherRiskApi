package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
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
import com.weatherrisk.api.model.Activity;
import com.weatherrisk.api.model.ActivityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MongoDB_Activity_Test {
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Test
	public void test_1_addActivity() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		System.out.println(cal.getTime());
		Activity activity = new Activity(1L, "Tommy", new Date(), "一起 Party", "一起喝酒喝喝喝", cal.getTime(), 25.075926F, 121.480557F, 10);
		activityRepository.insert(activity);
	}
	
	@Test
	public void test_2_FindActivityById() {
		long id = 1L;
		Activity activity = activityRepository.findById(id);
		System.out.println(activity);
		assertThat(activity).isNotNull();
		assertThat(activity.getId()).isEqualTo(id);
	}
	
	@Test
	public void test_3_FindActivityByCreateUser() {
		String createUser = "Tommy";
		Activity activity = activityRepository.findByCreateUser(createUser);
		System.out.println(activity);
		assertThat(activity).isNotNull();
		assertThat(activity.getCreateUser()).isEqualTo(createUser);
	}
	
	@Test
	public void test_4_DeleteAllActivities() {
		activityRepository.deleteAll();
	}
}
