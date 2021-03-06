package com.weatherrisk.api.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, Long> {
	Activity findById(Long id);
	
	List<Activity> findByCreateUser(String createUser);
	
	List<Activity> findByStartDatetimeBetween(Date startDate, Date endDate);
}
