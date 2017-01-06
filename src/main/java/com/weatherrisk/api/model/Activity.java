package com.weatherrisk.api.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

	public Activity() {
	}

	public Activity(Long id, String createUser, Date createDate, String title, String description, Date startDatetime,
			Float latitude, Float longitude, Integer attendeeNum) {
		this.id = id;
		this.createUser = createUser;
		this.createDate = createDate;
		this.title = title;
		this.description = description;
		this.startDatetime = startDatetime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.attendeeNum = attendeeNum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(Date startDatetime) {
		this.startDatetime = startDatetime;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Integer getAttendeeNum() {
		return attendeeNum;
	}

	public void setAttendeeNum(Integer attendeeNum) {
		this.attendeeNum = attendeeNum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Activity [id=").append(id).append(", createUser=").append(createUser).append(", createDate=")
				.append(createDate).append(", title=").append(title).append(", description=").append(description)
				.append(", startDatetime=").append(startDatetime).append(", latitude=").append(latitude)
				.append(", longitude=").append(longitude).append(", attendeeNum=").append(attendeeNum).append("]");
		return builder.toString();
	}
}
