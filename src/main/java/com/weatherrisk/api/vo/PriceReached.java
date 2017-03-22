package com.weatherrisk.api.vo;

import com.weatherrisk.api.cnst.CurrencyCnst;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceReached {
	private CurrencyCnst currency;
	private BigDecimal upperPrice;
	private BigDecimal lowerPrice;
}
