package com.weatherrisk.api.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "activities")
public class Activity {
	@Id
	private Long id;
	private String createUser;
	private Date createDate;
	private String title;
	private String description;
	private Date startDatetime;
	private Float latitude;
	private Float longitude;
	private Integer attendeeNum;

}
