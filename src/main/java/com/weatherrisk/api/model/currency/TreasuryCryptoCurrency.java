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

	private double totalVolumes;
	
	private double amount;
	
	public static String getId(String userId, String currencyCode) {
		return userId + "-" + currencyCode;
	}
	
	public void setNewData(CryptoCurrencyBSRecord bsRecord) {
		this.id = getId(bsRecord.getUserId(), bsRecord.getCurrencyCode());
		this.userId = bsRecord.getUserId();
		this.currencyCode = bsRecord.getCurrencyCode();
		this.avgPrice = bsRecord.getPrice();
		// 數量要扣掉手續費
		double volumeSubstractFee = new BigDecimal(bsRecord.getVolumes()).subtract(new BigDecimal(bsRecord.getFee())).doubleValue();
		this.totalVolumes = volumeSubstractFee;
		this.amount = bsRecord.getAmount();
	}
	
	public void buyUpdateExistData(CryptoCurrencyBSRecord bsRecord) {
		// 更新總數量
		double volumeSubstractFee = new BigDecimal(bsRecord.getVolumes()).subtract(new BigDecimal(bsRecord.getFee())).doubleValue();
		addTotalVolumes(volumeSubstractFee);
		
		// 加到原有的
		addAmount(bsRecord.getAmount());
		
		// 算出平均價格
		this.avgPrice = new BigDecimal(this.amount).divide(new BigDecimal(this.totalVolumes), 8, RoundingMode.DOWN).doubleValue();	
	}
	
	private void addTotalVolumes(double newVolume) {
		this.totalVolumes = new BigDecimal(this.totalVolumes).add(new BigDecimal(newVolume)).doubleValue();
	}
	
	private void addAmount(double newAmount) {
		this.amount = new BigDecimal(this.amount).add(new BigDecimal(newAmount)).doubleValue();
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

	public double getTotalVolumes() {
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
