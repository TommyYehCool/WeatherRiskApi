package com.weatherrisk.api.vo;

import com.weatherrisk.api.cnst.CurrencyCnst;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoCurrencyPriceReached {
	private CurrencyCnst currency;
	private BigDecimal lowerPrice;
	private BigDecimal upperPrice;
}
