package com.weatherrisk.api.model;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;

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
@Document(collection = "attractions")
public class Attraction {

	@Id
	@ApiModelProperty(notes = "id")
	private Long id;
	
	@ApiModelProperty(notes = "景點類別")
	private AttractionType attractionType;
	
	@ApiModelProperty(notes = "國家")
	private String country;
	
	@ApiModelProperty(notes = "名稱")
	private String name;
	
	@ApiModelProperty(notes = "經緯度")
	private Float[] loc;
	
	public Attraction() {
	}

	public Attraction(Long id, AttractionType attractionType, String country, String name, Float[] loc) {
		this.id = id;
		this.attractionType = attractionType;
		this.country = country;
		this.name = name;
		this.loc = loc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AttractionType getAttractionType() {
		return attractionType;
	}

	public void setAttractionType(AttractionType attractionType) {
		this.attractionType = attractionType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float[] getLoc() {
		return loc;
	}

	public void setLoc(Float[] loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Attraction [id=").append(id).append(", attractionType=").append(attractionType)
				.append(", country=").append(country).append(", name=").append(name).append(", loc=")
				.append(Arrays.toString(loc)).append("]");
		return builder.toString();
	}

}
