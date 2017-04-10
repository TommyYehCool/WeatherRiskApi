package com.weatherrisk.api.service.currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.vo.CryptoCurrencyPriceReached;

@Service
public class RegisterService {
	
	private Logger logger = LoggerFactory.getLogger(RegisterService.class);

	private Map<String, List<CryptoCurrencyPriceReached>> cryptoCurrencyRegisterMap = Collections.synchronizedMap(new HashMap<>());
	
	public void registerCryptoCurrency(String userId, CryptoCurrencyPriceReached priceReached) {
		List<CryptoCurrencyPriceReached> pricesReached;
		if (!this.cryptoCurrencyRegisterMap.containsKey(userId)) {
			pricesReached = new ArrayList<>();
			pricesReached.add(priceReached);
			this.cryptoCurrencyRegisterMap.put(userId, pricesReached);
		}
		else {
			pricesReached = this.cryptoCurrencyRegisterMap.get(userId);

			// 移除已經存在的幣別價格區間
			Iterator<CryptoCurrencyPriceReached> it = pricesReached.iterator();
			while (it.hasNext()) {
				CryptoCurrencyPriceReached existedPriceReached = it.next();
				if (existedPriceReached.getCurrency().equals(priceReached.getCurrency())) {
					it.remove();
				}
			}
			
			pricesReached.add(priceReached);
		}
		
		logger.info("UserId: <{}>, 註冊: {}", userId, pricesReached);
	}
	
	public void unregisterCryptoCurrency(String userId, CurrencyCnst currency) {
		List<CryptoCurrencyPriceReached> pricesReached = this.cryptoCurrencyRegisterMap.get(userId);
		Iterator<CryptoCurrencyPriceReached> it = pricesReached.iterator();
		while (it.hasNext()) {
			CryptoCurrencyPriceReached priceReached = it.next();
			if (priceReached.getCurrency().equals(currency)) {
				it.remove();
			}
		}
		if (pricesReached.size() == 0) {
			this.cryptoCurrencyRegisterMap.remove(userId);
			logger.info("UserId: <{}>, 取消所有到價通知", userId);
		}
		else {
			logger.info("UserId: <{}>, 仍有註冊: {}", userId, pricesReached);
		}
	}
	
	public boolean hasRegisteredCryptoCurrency(String userId) {
		if (this.cryptoCurrencyRegisterMap.containsKey(userId)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasRegisteredCryptoCurrency(String userId, CurrencyCnst currency) {
		if (this.cryptoCurrencyRegisterMap.containsKey(userId)) {
			List<CryptoCurrencyPriceReached> pricesReached = this.cryptoCurrencyRegisterMap.get(userId);
			for (CryptoCurrencyPriceReached priceReached : pricesReached) {
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
	
	public String getCryptoCurrencyPricesReachedInfos(String userId) {
		List<CryptoCurrencyPriceReached> pricesReached = this.cryptoCurrencyRegisterMap.get(userId);
		StringBuilder buffer = new StringBuilder();
		for (CryptoCurrencyPriceReached priceReached : pricesReached) {
			buffer.append("貨幣: ").append(priceReached.getCurrency()).append(" => ");
			buffer.append(priceReached.getLowerPrice().doubleValue()).append(" ~ ").append(priceReached.getUpperPrice().doubleValue());
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	public Map<String, List<CryptoCurrencyPriceReached>> getCryptoCurrencyRegisterInfos() {
		return this.cryptoCurrencyRegisterMap;
	}
}
