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
	
	/**
	 * 非 BTC 新資料
	 * 
	 * @param bsRecord
	 */
	public void setNewDataNonBtc(CryptoCurrencyBSRecord bsRecord) {
		this.id = getId(bsRecord.getUserId(), bsRecord.getCurrencyCode());
		this.userId = bsRecord.getUserId();
		this.currencyCode = bsRecord.getCurrencyCode();
		this.avgPrice = bsRecord.getPrice();
		// 數量要扣掉手續費
		double buyVolumeSubFee = getBuyVolumeSubFee(bsRecord.getVolumes(), bsRecord.getFee());
		this.totalVolumes = buyVolumeSubFee;
		this.amount = bsRecord.getAmount();
	}
	
	/**
	 * TODO BTC 買更新
	 * 
	 * @param bsRecord
	 */
	public void buyUpdateExistDataBtc(CryptoCurrencyBSRecord bsRecord) {
		
	}

	/**
	 * 非 BTC 買更新
	 * 
	 * @param bsRecord
	 */
	public void buyUpdateExistDataNonBtc(CryptoCurrencyBSRecord bsRecord) {
		// 算出新數量扣掉手續費
		double buyVolumeSubFee = getBuyVolumeSubFee(bsRecord.getVolumes(), bsRecord.getFee());
		
		// 加到總數量
		addTotalVolumes(buyVolumeSubFee);
		
		// 加到總價金
		addAmount(bsRecord.getAmount());
		
		// 算出平均價格
		calAvgPrice();	
	}
	
	/**
	 * 非 BTC 賣更新
	 * 
	 * @param bsRecord
	 */
	public void sellUpdateExistDataNonBtc(CryptoCurrencyBSRecord bsRecord) {
		// 扣掉總數量
		subTotalVolumes(bsRecord.getVolumes());
		
		// FIXME 先把總價金清成 0
		this.amount = 0;
		
		// FIXME 先把平均價格清成 0
		this.avgPrice = 0;
	}

	/**
	 * BTC 賣更新
	 * 
	 * @param bsRecord
	 */
	public void sellUpdateExistDataBtc(CryptoCurrencyBSRecord bsRecord) {
		// 扣掉總數量
		addTotalVolumes(bsRecord.getAmount());
	}
	
	private double getBuyVolumeSubFee(double buyVolumes, double fee) {
		BigDecimal bBuyVolumes = new BigDecimal(String.valueOf(buyVolumes));
		BigDecimal bFee = new BigDecimal(String.valueOf(fee));
		return bBuyVolumes.subtract(bFee).doubleValue();
	}
	
	private void addTotalVolumes(double volumes) {
		BigDecimal bTotalVolumes = new BigDecimal(String.valueOf(this.totalVolumes));
		BigDecimal bVolumes = new BigDecimal(String.valueOf(volumes));
		this.totalVolumes = bTotalVolumes.add(bVolumes).doubleValue();
	}
	
	private void addAmount(double amount) {
		BigDecimal bOrigAmount = new BigDecimal(String.valueOf(this.amount));
		BigDecimal bAmount = new BigDecimal(String.valueOf(amount));
		this.amount = bOrigAmount.add(bAmount).doubleValue();
	}
	
	private void calAvgPrice() {
		BigDecimal bAmount = new BigDecimal(String.valueOf(this.amount));
		BigDecimal bTotalVolumes = new BigDecimal(String.valueOf(this.totalVolumes));
		BigDecimal bAvgPrice = bAmount.divide(bTotalVolumes, 8, RoundingMode.CEILING);
		this.avgPrice = bAvgPrice.doubleValue();
	}
	
	private void subTotalVolumes(double volumes) {
		BigDecimal bTotalVolumes = new BigDecimal(String.valueOf(this.totalVolumes));
		BigDecimal bVolumes = new BigDecimal(String.valueOf(volumes));
		this.totalVolumes = bTotalVolumes.subtract(bVolumes).doubleValue();
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
