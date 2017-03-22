package com.weatherrisk.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.weatherrisk.api.vo.PriceReached;

@Service
public class RegisterService {

	private Map<String, PriceReached> cryptoRegisterMap = new HashMap<>();
	
	public void register(String userId, PriceReached priceReached) {
		this.cryptoRegisterMap.put(userId, priceReached);
	}
	
	public void unregister(String userId) {
		this.cryptoRegisterMap.remove(userId);
	}
}
