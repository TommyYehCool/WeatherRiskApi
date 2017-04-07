package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "otc_stock")
public class OtcStock extends Stock {

}
