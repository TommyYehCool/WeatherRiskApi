package com.weatherrisk.api.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * <pre>
 * 參考: <a href="http://blog.didispace.com/springbootmongodb/">Spring Boot MongoDB</a>
 * 
 * 參考: <a href="https://tests4geeks.com/spring-data-boot-mongodb-example/">Spring data boot mongodb example</a>
 * </pre>
 * 
 * @author tommy.feng
 *
 */
public interface AttractionRepository extends MongoRepository<Attraction, Long> {
	
	List<Attraction> findAttractionsByAttractionType(AttractionType attractionType);
}
