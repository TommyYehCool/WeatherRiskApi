package com.weatherrisk.api.model.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "treasury_crypto_currency")
public class TreasuryCryptoCurrency {
	
	@Transient
	private DecimalFormat decFormat = new DecimalFormat("0.00000000");
	
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
		double volumeSubFee = getVolumeSubFee(bsRecord.getVolumes(), bsRecord.getFee());
		this.totalVolumes = volumeSubFee;
		this.amount = bsRecord.getAmount();
	}
	
	public void buyUpdateExistData(CryptoCurrencyBSRecord bsRecord) {
		// 算出新數量扣掉手續費
		double newVolume = getVolumeSubFee(bsRecord.getVolumes(), bsRecord.getFee());
		
		// 加到總數量
		addTotalVolumes(newVolume);
		
		// 加到總價金
		addAmount(bsRecord.getAmount());
		
		// 算出平均價格
		calAvgPrice();	
	}
	
	private double getVolumeSubFee(double volumes, double fee) {
		BigDecimal bVolumes = new BigDecimal(String.valueOf(volumes));
		BigDecimal bFee = new BigDecimal(String.valueOf(fee));
		double volumeSubFee = bVolumes.subtract(bFee).doubleValue();
		return volumeSubFee;
	}
	
	private void addTotalVolumes(double newVolume) {
		BigDecimal bTotalVolumes = new BigDecimal(String.valueOf(this.totalVolumes));
		BigDecimal bNewVolumes = new BigDecimal(String.valueOf(newVolume));
		this.totalVolumes = bTotalVolumes.add(bNewVolumes).doubleValue();
	}
	
	private void addAmount(double newAmount) {
		BigDecimal bOrigAmount = new BigDecimal(String.valueOf(this.amount));
		BigDecimal bNewAmount = new BigDecimal(String.valueOf(newAmount));
		BigDecimal bAmount = bOrigAmount.add(bNewAmount);
		this.amount = bAmount.doubleValue();
	}
	
	private void calAvgPrice() {
		BigDecimal bAmount = new BigDecimal(String.valueOf(this.amount));
		BigDecimal bTotalVolumes = new BigDecimal(String.valueOf(this.totalVolumes));
		BigDecimal bAvgPrice = bAmount.divide(bTotalVolumes, 8, RoundingMode.CEILING);
		this.avgPrice = bAvgPrice.doubleValue();
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
