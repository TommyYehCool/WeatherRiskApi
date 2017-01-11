package com.weatherrisk.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.model.Attraction;
import com.weatherrisk.api.model.AttractionRepository;
import com.weatherrisk.api.model.AttractionType;

@Service
public class AttractionService {
	
	@Autowired
	private AttractionRepository attractionRepo;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Attraction> queryAttrationsByType(AttractionType type) {
		return attractionRepo.findAttractionsByAttractionType(type);
	}
}
