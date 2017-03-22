package com.weatherrisk.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.vo.PriceReached;

@Service
public class RegisterService {

	private Map<String, List<PriceReached>> cryptoRegisterMap = new HashMap<>();
	
	public void register(String userId, PriceReached priceReached) {
		List<PriceReached> pricesReached;
		if (!this.cryptoRegisterMap.containsKey(userId)) {
			pricesReached = new ArrayList<>();
			pricesReached.add(priceReached);
			this.cryptoRegisterMap.put(userId, pricesReached);
		}
		else {
			pricesReached = this.cryptoRegisterMap.get(userId);
			pricesReached.add(priceReached);
		}
	}
	
	public void unregister(String userId) {
		this.cryptoRegisterMap.remove(userId);
	}
	
	public boolean hasRegistered(String userId, CurrencyCnst currency) {
		if (this.cryptoRegisterMap.containsKey(userId)) {
			return true;
		}
		else {
			return false;
		}
	}
}
