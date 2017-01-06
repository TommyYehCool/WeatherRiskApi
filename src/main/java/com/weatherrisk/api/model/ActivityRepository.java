package com.weatherrisk.api.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, Long> {
	Activity findById(Long id);
	
	Activity findByCreateUser(String createUser);
}
