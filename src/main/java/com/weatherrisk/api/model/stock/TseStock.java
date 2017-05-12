package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.core.mapping.Document;

import com.weatherrisk.api.cnst.stock.StockType;

@Document(collection = "tse_stock")
public class TseStock extends Stock {

	public TseStock() {
		super.setStockType(StockType.TSE);
	}
}
