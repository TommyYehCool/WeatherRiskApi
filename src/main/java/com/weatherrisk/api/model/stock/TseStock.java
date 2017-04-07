package com.weatherrisk.api.model.stock;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tse_stock")
public class TseStock extends Stock {

}
