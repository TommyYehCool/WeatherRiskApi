package com.weatherrisk.api.model.stock;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.weatherrisk.api.cnst.StockType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "treasury_stock")
public class TreasuryStock {

	@Transient
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	@Transient
	private final double feePercent = 0.001425;
	
	private String userId;
	
	private StockType stockType;
	
	private String id;
	
	private String name;
	
	private Date buyDate;

	private double buyPrice;

	private long buyShares;
	
	private long buyMatchAmount;

	private Date sellDate;
	
	private double sellPrice;

	private long sellShares;
	
	private long sellMatchAmount;
	
	public void setBuyDate(String buyDate) throws ParseException {
		this.buyDate = dateFormat.parse(buyDate);
	}

	public void setBuyPriceAndShares(double buyPrice, int buyShares) {
		this.buyPrice = buyPrice;
		this.buyShares = buyShares;
		
		BigDecimal matchAmountWithoutFee = new BigDecimal(this.buyPrice).multiply(new BigDecimal(this.buyShares));
		this.buyMatchAmount = matchAmountWithoutFee.add(matchAmountWithoutFee.multiply(new BigDecimal(feePercent))).longValue();
	}
	
	public void setSellDate(String sellDate) throws ParseException {
		this.sellDate = dateFormat.parse(sellDate);
	}
	
	public void setSellPriceAndShares(double sellPrice, int sellShares) {
		this.sellPrice = sellPrice;
		this.sellShares = sellShares;
		
		BigDecimal matchAmountWithoutFee = new BigDecimal(this.sellPrice).multiply(new BigDecimal(this.sellShares));
		this.sellMatchAmount = matchAmountWithoutFee.subtract(matchAmountWithoutFee.multiply(new BigDecimal(feePercent))).longValue();
	}

	@Override
	public String toString() {
		return "TreasuryStock [feePercent=" + feePercent + ", stockType=" + stockType + ", id=" + id + ", name=" + name
				+ ", buyDate=" + buyDate + ", buyPrice=" + buyPrice + ", buyShares=" + buyShares + ", buyMatchAmount="
				+ buyMatchAmount + ", sellDate=" + sellDate + ", sellPrice=" + sellPrice + ", sellShares=" + sellShares
				+ ", sellMatchAmount=" + sellMatchAmount + "]";
	}

}
