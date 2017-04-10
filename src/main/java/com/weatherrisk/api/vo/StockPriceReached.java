package com.weatherrisk.api.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockPriceReached {
	private String stockNameOrId;
	private boolean isName;
	private BigDecimal lowerPrice;
	private BigDecimal upperPrice;

	public StockPriceReached(String stockNameOrId, BigDecimal lowerPrice, BigDecimal upperPrice) {
		this.stockNameOrId = stockNameOrId;
		this.isName = !StringUtils.isNumeric(stockNameOrId);
		this.lowerPrice = lowerPrice;
		this.upperPrice = upperPrice;
	}
}
