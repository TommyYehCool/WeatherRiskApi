package com.weatherrisk.api.model;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 參考: <a href="http://blog.didispace.com/springbootmongodb/">Spring Boot MongoDB</a>
 * 
 * @author tommy.feng
 *
 */
public interface CityRepository extends MongoRepository<City, Long> {
	City findByCity(String city);
}
