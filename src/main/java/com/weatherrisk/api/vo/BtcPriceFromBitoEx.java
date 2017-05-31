package com.weatherrisk.api.vo;

import java.math.BigDecimal;

public class BtcPriceFromBitoEx {
	private BigDecimal buyPrice;
	private BigDecimal sellPrice;

	public BtcPriceFromBitoEx(BigDecimal buyPrice, BigDecimal sellPrice) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
}