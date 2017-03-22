package com.weatherrisk.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.vo.PriceReached;

@Service
public class RegisterService {
	
	private Logger logger = LoggerFactory.getLogger(RegisterService.class);

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
		
		logger.info("UserId: <{}>, 註冊: {}", userId, pricesReached);
	}
	
	public void unregister(String userId, CurrencyCnst currency) {
		List<PriceReached> pricesReached = this.cryptoRegisterMap.get(userId);
		Iterator<PriceReached> it = pricesReached.iterator();
		while (it.hasNext()) {
			PriceReached priceReached = it.next();
			if (priceReached.getCurrency().equals(currency)) {
				it.remove();
			}
		}
		if (pricesReached.size() == 0) {
			logger.info("UserId: <{}>, 取消所有到價通知", userId);
		}
		else {
			logger.info("UserId: <{}>, 仍有註冊: {}", userId, pricesReached);
		}
	}
	
	public boolean hasRegistered(String userId, CurrencyCnst currency) {
		if (this.cryptoRegisterMap.containsKey(userId)) {
			List<PriceReached> pricesReached = this.cryptoRegisterMap.get(userId);
			for (PriceReached priceReached : pricesReached) {
				if (priceReached.getCurrency().equals(currency)) {
					return true;
				}
			}
			return false;
		}
		else {
			return false;
		}
	}
}
