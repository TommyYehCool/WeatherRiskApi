package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.core.mapping.Document;

import com.weatherrisk.api.cnst.stock.StockType;

@Document(collection = "otc_stock")
public class OtcStock extends Stock {

	public OtcStock() {
		super.setStockType(StockType.OTC);
	}
}
