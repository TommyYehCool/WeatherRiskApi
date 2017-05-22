package com.weatherrisk.api.model.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.weatherrisk.api.cnst.BuySell;

@Document(collection = "crypto_currency_bs_record")
public class CryptoCurrencyBSRecord {
	
	@Transient
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
	
	@Transient
	private final BigDecimal buyFeePercent = new BigDecimal(0.0015);
	
	@Transient
	private final BigDecimal sellFeePercent = new BigDecimal(0.0025);
	
	@Id
	private String id;
	
	private String userId;
	
	private String currencyCode;
	
	private Date dateTime;
	
	private BuySell buySell;
	
	private double price;
	
	private double volumes;
	
	private double fee;
	
	private double amount;
	
	public static String getId(String userId, String currencyCode, String dateTime, BuySell buySell) {
		return userId + "-" + currencyCode + "-" + buySell + "-" + dateTime;
	}
	
	public void setData(String userId, String currencyCode, BuySell buySell, String dateTime, BigDecimal price, BigDecimal volumes) throws ParseException {
		this.id = getId(userId, currencyCode, dateTime, buySell);
		this.userId = userId;
		this.currencyCode = currencyCode;
		this.dateTime = dateTimeFormat.parse(dateTime);
		this.buySell = buySell;
		this.price = price.setScale(8, RoundingMode.DOWN).doubleValue();
		this.volumes = volumes.doubleValue();
		this.fee = 
			BuySell.BUY == buySell ? 
				volumes.multiply(buyFeePercent).setScale(8, RoundingMode.HALF_UP).doubleValue() :
				price.multiply(volumes).multiply(sellFeePercent).setScale(8, RoundingMode.HALF_UP).doubleValue();
		this.amount = 
			BuySell.BUY == buySell ?
				price.multiply(volumes).setScale(8, RoundingMode.DOWN).doubleValue() :
				price.multiply(volumes).subtract(new BigDecimal(String.valueOf(this.fee))).setScale(8, RoundingMode.FLOOR).doubleValue();
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

	public Date getDateTime() {
		return dateTime;
	}

	public BuySell getBuySell() {
		return buySell;
	}

	public double getPrice() {
		return price;
	}

	public double getVolumes() {
		return volumes;
	}
	
	public double getFee() {
		return fee;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "CryptoCurrencyBSRecord [id=" + id + ", userId=" + userId + ", currencyCode=" + currencyCode
				+ ", dateTime=" + dateTime + ", buySell=" + buySell + ", price=" + price + ", volumes=" + volumes
				+ ", fee=" + fee + ", amount=" + amount + "]";
	}

}
