package com.weatherrisk.api.model;

import org.springframework.data.annotation.Id;

public class City {

	@Id
	private Long id;
	private String city;
	private String state;
	private Integer pop;
	private Float[] loc;

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

}
