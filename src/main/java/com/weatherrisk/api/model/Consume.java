package com.weatherrisk.api.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consumes")
public class Consume {
	
	@Id
	private String lotteryNo;
	private String user;
	private Date consumeDate;
	private Integer type;
	private String prodName;
	private Long amount;
	private Long prize;
	private Boolean got;
	private Boolean alreadySent;
	
	public Consume() {
	}
	
	public Consume(String lotteryNo, String user, Date consumeDate, Integer type, String prodName, Long amount,
			Long prize, Boolean got, Boolean alreadySent) {
		this.lotteryNo = lotteryNo;
		this.user = user;
		this.consumeDate = consumeDate;
		this.type = type;
		this.prodName = prodName;
		this.amount = amount;
		this.prize = prize;
		this.got = got;
		this.alreadySent = alreadySent;
	}

	public String getLotteryNo() {
		return lotteryNo;
	}

	public void setLotteryNo(String lotteryNo) {
		this.lotteryNo = lotteryNo;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getConsumeDate() {
		return consumeDate;
	}

	public void setConsumeDate(Date consumeDate) {
		this.consumeDate = consumeDate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getPrize() {
		return prize;
	}

	public void setPrize(Long prize) {
		this.prize = prize;
	}

	public Boolean getGot() {
		return got;
	}

	public void setGot(Boolean got) {
		this.got = got;
	}

	public Boolean getAlreadySent() {
		return alreadySent;
	}

	public void setAlreadySent(Boolean alreadySent) {
		this.alreadySent = alreadySent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Consume [lotteryNo=").append(lotteryNo).append(", user=").append(user).append(", consumeDate=")
				.append(consumeDate).append(", type=").append(type).append(", prodName=").append(prodName)
				.append(", amount=").append(amount).append(", prize=").append(prize).append(", got=").append(got)
				.append(", alreadySent=").append(alreadySent).append("]");
		return builder.toString();
	}
}
