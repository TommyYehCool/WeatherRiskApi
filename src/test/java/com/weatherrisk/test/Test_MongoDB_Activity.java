package com.weatherrisk.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.mongodb.core.mapping.Document;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.model.Activity;
import com.weatherrisk.api.model.ActivityRepository;
import com.weatherrisk.api.service.CounterService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MongoDB_Activity {
	
	@Autowired
	private CounterService counterService;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	private final String collectionName = ((Document) Activity.class.getAnnotation(Document.class)).collection();
	
	@Test
	public void test_1_deleteAllActivities() {
		activityRepository.deleteAll();
		
		List<Activity> allActivities = activityRepository.findAll();
		assertThat(allActivities.size()).isEqualTo(0);
		
		Long currentSeqNo = counterService.resetSequence(collectionName);
		assertThat(currentSeqNo).isEqualTo(0L);
		
		System.out.println(">>>>> Test 1: deleteAllActivities -> Delete all testing datas and reset counters done");
	}
	
	@Test
	public void test_2_addActivities() throws Exception {
		// ----- Add 第一筆 -----
		Long id = counterService.getNextSequence(collectionName);
		String createUser = "Tommy";
		Date createDate = new Date();
		String title = "一起 Party";
		String description = "一起喝酒喝喝喝";
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		Date startDatetime = cal.getTime();
		
		Float latitude = 25.075926F;
		Float longitude = 121.480557F;
		Integer attendeeNum = 10;
		
		activityRepository.save(new Activity(id, createUser, createDate, title, description, startDatetime, latitude, longitude, attendeeNum));
		
		// ----- Add 第二筆 -----
		id = counterService.getNextSequence(collectionName);
		title = "Tommy 生日趴";
		description = "一起 Happy";
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		startDatetime = dateTimeFormat.parse("2017/11/12 18:00");
		
		attendeeNum = 20;
		
		activityRepository.save(new Activity(id, createUser, createDate, title, description, startDatetime, latitude, longitude, attendeeNum));
		
		System.out.println(">>>>> Test 2: addActivity -> Add testing datas done, total datas: " + id);
	}
	
	@Test
	public void test_3_findActivityById() {
		long id = 1L;
		Activity activity = activityRepository.findById(id);
		
		assertThat(activity).isNotNull();
		assertThat(activity.getId()).isEqualTo(id);
		
		System.out.println(">>>>> Test 3: findActivityById(" + id + ") -> " + activity);
	}
	
	@Test
	public void test_4_findActivitiesByCreateUser() {
		String createUser = "Tommy";
		
		List<Activity> activities = activityRepository.findByCreateUser(createUser);

		assertThat(activities.size()).isEqualTo(2);
		
		for (Activity activity : activities) {
			assertThat(activity.getCreateUser()).isEqualTo(createUser);
		}
		
		System.out.println(">>>>> Test 4: findActivityByCreateUser(" + createUser + ") -> " + activities);
	}
	
	@Test
	public void test_5_findActivitiesByStartDatetimeBetween() throws Exception {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date startDate = dateTimeFormat.parse("2017/02/01 00:00");
		Date endDate = dateTimeFormat.parse("2017/12/01 00:00");
		
		List<Activity> activities = activityRepository.findByStartDatetimeBetween(startDate, endDate);
		
		assertThat(activities.size()).isEqualTo(1);
		
		System.out.println(">>>>> Test 5: test_5_findActivitiesByStartDateBetween(" + startDate + ", " + endDate + ") -> " + activities);
	}
}
