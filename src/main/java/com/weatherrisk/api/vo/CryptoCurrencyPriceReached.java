package com.weatherrisk.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.weatherrisk.api.cnst.currency.CurrencyCnst;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoCurrencyPriceReached {
	private CurrencyCnst currency;
	private BigDecimal lowerPrice;
	private BigDecimal upperPrice;
}
