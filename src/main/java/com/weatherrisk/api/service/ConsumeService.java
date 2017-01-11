package com.weatherrisk.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.model.Consume;

@Service
public class ConsumeService {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Consume> queryConsumesByProdName(String prodName) {
		Query query = new Query();
		
		query.addCriteria(Criteria.where("prodName").is(prodName));

		List<Consume> consumes = mongoTemplate.find(query, Consume.class);

		return consumes;
	}
}
