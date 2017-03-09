package com.weatherrisk.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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
	
}
