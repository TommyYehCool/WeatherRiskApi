package com.weatherrisk.api.model.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
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
	public static final double feePercent = 0.001425;
	
	// 賣交易稅
	@Transient
	public static final double sellTradeTaxPercent = 0.003;

	@Id
	private String id;
	
	private String userId;
	
	private StockType stockType;
	
	private String stockId;
	
	private String stockName;
	
	private Date buyDate;

	private double buyPrice;

	private long buyShares;
	
	private double buyMatchAmount;

	private Date sellDate;
	
	private double sellPrice;

	private long sellShares;
	
	private double sellMatchAmount;
	
	public void setBuyDate(String buyDate) throws ParseException {
		this.buyDate = dateFormat.parse(buyDate);
	}

	public void setBuyPriceAndShares(double buyPrice, long buyShares) {
		this.buyPrice = buyPrice;
		this.buyShares = buyShares;
		
		BigDecimal bBuyPrice = new BigDecimal(this.buyPrice);
		BigDecimal bBuyShares = new BigDecimal(this.buyShares);
		BigDecimal matchAmountWithoutFee = bBuyPrice.multiply(bBuyShares);
		
		BigDecimal fee = matchAmountWithoutFee.multiply(new BigDecimal(feePercent)).setScale(0, RoundingMode.FLOOR);
		
		this.buyMatchAmount = matchAmountWithoutFee.add(fee).doubleValue();
	}
	
	public void setSellDate(String sellDate) throws ParseException {
		this.sellDate = dateFormat.parse(sellDate);
	}
	
	public void setSellPriceAndShares(double sellPrice, long sellShares) {
		this.sellPrice = sellPrice;
		this.sellShares = sellShares;
		
		BigDecimal bSellPrice = new BigDecimal(this.sellPrice);
		BigDecimal bSellShares = new BigDecimal(this.sellShares);
		BigDecimal matchAmountWithoutFee = bSellPrice.multiply(bSellShares);
		
		BigDecimal fee = matchAmountWithoutFee.multiply(new BigDecimal(feePercent)).setScale(0, RoundingMode.FLOOR);
		
		BigDecimal sellTradeTax = matchAmountWithoutFee.multiply(new BigDecimal(sellTradeTaxPercent)).setScale(0, RoundingMode.FLOOR);
		
		this.sellMatchAmount = matchAmountWithoutFee.subtract(fee).subtract(sellTradeTax).doubleValue();
	}

	@Override
	public String toString() {
		return "TreasuryStock [id=" + id + ", userId=" + userId + ", stockType=" + stockType + ", stockId=" + stockId
				+ ", stockName=" + stockName + ", buyDate=" + buyDate + ", buyPrice=" + buyPrice + ", buyShares="
				+ buyShares + ", buyMatchAmount=" + buyMatchAmount + ", sellDate=" + sellDate + ", sellPrice="
				+ sellPrice + ", sellShares=" + sellShares + ", sellMatchAmount=" + sellMatchAmount + "]";
	}
}
