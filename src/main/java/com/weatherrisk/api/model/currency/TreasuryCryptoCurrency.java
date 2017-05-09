package com.weatherrisk.api.model.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "treasury_crypto_currency")
public class TreasuryCryptoCurrency {
	
	@Id
	private String id;
	
	private String userId;
	
	private String currencyCode;
	
	private double avgPrice;

	private long totalVolumes;
	
	private double amount;
	
	public static String getId(String userId, String currencyCode) {
		return userId + "-" + currencyCode;
	}
	
	public void setNewData(String userId, String currencyCode, BigDecimal price, BigDecimal volumes) {
		this.id = getId(userId, currencyCode);
		this.userId = userId;
		this.currencyCode = currencyCode;
		this.avgPrice = price.setScale(8, RoundingMode.DOWN).doubleValue();
		this.totalVolumes = volumes.setScale(8, RoundingMode.DOWN).longValue();
		this.amount = price.multiply(volumes).setScale(8, RoundingMode.DOWN).doubleValue();
	}
	
	public void buyUpdateExistData(BigDecimal price, BigDecimal volumes) {
		// 更新總數量
		this.totalVolumes = new BigDecimal(this.totalVolumes).add(volumes).longValue();
		
		// 算出新的一筆價金
		BigDecimal newAmount = price.multiply(volumes).setScale(8, RoundingMode.DOWN);
		
		// 加到原有的
		this.amount = new BigDecimal(this.amount).add(newAmount).setScale(8, RoundingMode.DOWN).doubleValue();
		
		// 算出平均價格
		this.avgPrice = new BigDecimal(this.amount).divide(new BigDecimal(this.totalVolumes), 8, RoundingMode.DOWN).doubleValue();	
	}

	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public long getTotalVolumes() {
		return totalVolumes;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "TreasuryCryptoCurrency [id=" + id + ", userId=" + userId + ", currencyCode=" + currencyCode
				+ ", avgPrice=" + avgPrice + ", totalVolumes=" + totalVolumes + ", amount=" + amount + "]";
	}

}
