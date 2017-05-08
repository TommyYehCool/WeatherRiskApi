package com.weatherrisk.api.model.currency;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "treasury_crypto_currency")
public class TreasuryCryptoCurrency {
	
	@Transient
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	@Id
	private String id;
	
	private String userId;
	
	private String currencyCode;
	
	private Date buyDate;

	private double buyPrice;

	private long buyVolumes;
	
	private double buyMatchAmount;

	private Date sellDate;
	
	private double sellPrice;

	private long sellVolumes;
	
	private double sellMatchAmount;
	
	public void setBuyDate(String buyDate) throws ParseException {
		this.buyDate = dateFormat.parse(buyDate);
	}

	public void setBuyPriceAndVolumes(double buyPrice, long buyVolumes) {
		this.buyPrice = buyPrice;
		this.buyVolumes = buyVolumes;
		
		BigDecimal bBuyPrice = new BigDecimal(this.buyPrice);
		BigDecimal bBuyVolumes = new BigDecimal(this.buyVolumes);
		BigDecimal matchAmountWithoutFee = bBuyPrice.multiply(bBuyVolumes);
		
		this.buyMatchAmount = matchAmountWithoutFee.doubleValue();
	}
	
	public void setSellDate(String sellDate) throws ParseException {
		this.sellDate = dateFormat.parse(sellDate);
	}
	
	public void setSellPriceAndVolumes(double sellPrice, long sellVolumes) {
		this.sellPrice = sellPrice;
		this.sellVolumes = sellVolumes;
		
		BigDecimal bSellPrice = new BigDecimal(this.sellPrice);
		BigDecimal bSellVolumes = new BigDecimal(this.sellVolumes);
		BigDecimal matchAmountWithoutFee = bSellPrice.multiply(bSellVolumes);
		
		this.sellMatchAmount = matchAmountWithoutFee.doubleValue();
	}

}
