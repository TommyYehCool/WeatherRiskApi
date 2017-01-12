package com.weatherrisk.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.model.Attraction;
import com.weatherrisk.api.model.AttractionRepository;
import com.weatherrisk.api.model.AttractionType;

@Service
public class AttractionService {
	
	@Autowired
	private CounterService counterService;

	@Autowired
	private AttractionRepository attractionRepo;

	@Autowired
	private MongoTemplate mongoTemplate;

	private String collectionName = ((Document) Attraction.class.getAnnotation(Document.class)).collection();

	public void add(AttractionType attractionType, String country, String name, Float latitude, Float longitude) {
		Long id = getNextSeq();
		Float[] loc = new Float[] {latitude, longitude};
		attractionRepo.save(new Attraction(id, attractionType, country, name, loc));
	}
	
	public void delete(Long id) {
		attractionRepo.delete(id);
	}

	public List<Attraction> queryByType(AttractionType type) {
		return attractionRepo.findAttractionsByAttractionType(type);
	}
	
	private Long getNextSeq() {
		return counterService.getNextSequence(collectionName);
	}

}
