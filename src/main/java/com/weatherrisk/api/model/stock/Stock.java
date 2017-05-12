package com.weatherrisk.api.model.stock;

import com.weatherrisk.api.cnst.stock.StockType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

	protected String id;
	
	protected String name;
	
	protected StockType stockType;
}