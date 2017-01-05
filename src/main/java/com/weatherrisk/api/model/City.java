package com.weatherrisk.api.model;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "citys")
public class City {

	@Id
	private Long id;
	private String city;
	private String state;
	private Integer pop;
	private Float[] loc;
	
	public City() {
	}
	
	public City(String city, String state, Integer pop, Float[] loc) {
		this.city = city;
		this.state = state;
		this.pop = pop;
		this.loc = loc;
	}
	
	public City(Long id, String city, String state, Integer pop, Float[] loc) {
		this.id = id;
		this.city = city;
		this.state = state;
		this.pop = pop;
		this.loc = loc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getPop() {
		return pop;
	}

	public void setPop(Integer pop) {
		this.pop = pop;
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
		builder.append("City [id=").append(id).append(", city=").append(city).append(", state=").append(state)
				.append(", pop=").append(pop).append(", loc=").append(Arrays.toString(loc)).append("]");
		return builder.toString();
	}

}
