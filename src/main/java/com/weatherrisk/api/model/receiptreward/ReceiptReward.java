package com.weatherrisk.api.model.receiptreward;

import org.springframework.data.mongodb.core.mapping.Document;

import com.exfantasy.utils.tools.receipt_lottery.RewardType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "receipt_rewards")
public class ReceiptReward {
	private String section;
	private RewardType rewardType;
	private String no;
}
