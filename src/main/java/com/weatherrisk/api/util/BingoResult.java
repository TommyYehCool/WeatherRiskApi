package com.weatherrisk.api.util;

import com.exfantasy.utils.tools.receipt_lottery.RewardType;

import lombok.Data;

@Data
public class BingoResult {
	private String section;
	private RewardType rewardType;
	private String rewardNo;
	private BingoStatus bingoStatus = BingoStatus.NOT_GOT;
	private Long prize;

	public String getSectionStr() {
		String[] yearMonths = section.split("_");
		String year = yearMonths[0];
		String months = yearMonths[1];

		StringBuilder buffer = new StringBuilder();
		buffer.append(year).append("年").append(months).append("月");

		return buffer.toString();
	}
	
	public enum BingoStatus { 
		NOT_GOT, GOT, MAYBE;
	}
}