package com.weatherrisk.api.service.currency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.vo.CryptoCurrencyPriceReached;
import com.weatherrisk.api.vo.StockPriceReached;

@Service
public class RegisterService {
	
	private Logger logger = LoggerFactory.getLogger(RegisterService.class);
	/**
	 * 數位貨幣註冊紀錄
	 */
	private Map<String, List<CryptoCurrencyPriceReached>> cryptoCurrencyRegisterMap = Collections.synchronizedMap(new HashMap<>());
	/**
	 * 股票註冊紀錄
	 */
	private Map<String, List<StockPriceReached>> stockRegisterMap = Collections.synchronizedMap(new HashMap<>());
	
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
	
	public void registerStock(String userId, StockPriceReached stockPriceReached) {
		List<StockPriceReached> pricesReached;
		if (!this.stockRegisterMap.containsKey(userId)) {
			pricesReached = new ArrayList<>();
			pricesReached.add(stockPriceReached);
			this.stockRegisterMap.put(userId, pricesReached);
		}
		else {
			pricesReached = this.stockRegisterMap.get(userId);
			
			// 移除已經存在的股票價格區間
			Iterator<StockPriceReached> it = pricesReached.iterator();
			while (it.hasNext()) {
				StockPriceReached existedPriceReached = it.next();
				if (existedPriceReached.getStockNameOrId().equals(stockPriceReached.getStockNameOrId())) {
					it.remove();
				}
			}
			
			pricesReached.add(stockPriceReached);
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
	
	public void unregisterStockPrice(String userId, String stockNameOrId) {
		List<StockPriceReached> pricesReached = this.stockRegisterMap.get(userId);
		Iterator<StockPriceReached> it = pricesReached.iterator();
		while (it.hasNext()) {
			StockPriceReached priceReached = it.next();
			if (priceReached.getStockNameOrId().equals(stockNameOrId)) {
				it.remove();
			}
		}
		if (pricesReached.size() == 0) {
			this.stockRegisterMap.remove(userId);
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
	
	public boolean hasRegisteredStock(String userId) {
		if (this.stockRegisterMap.containsKey(userId)) {
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
	
	public boolean hasRegisteredStock(String userId, String stockNameOrId) {
		if (this.stockRegisterMap.containsKey(userId)) {
			List<StockPriceReached> pricesReached = this.stockRegisterMap.get(userId);
			for (StockPriceReached priceReached : pricesReached) {
				if (priceReached.getStockNameOrId().equals(stockNameOrId)) {
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
	
	public String getStockPricesReachedInfos(String userId) {
		List<StockPriceReached> pricesReached = this.stockRegisterMap.get(userId);
		StringBuilder buffer = new StringBuilder();
		for (StockPriceReached priceReached : pricesReached) {
			buffer.append(priceReached.getStockNameOrId()).append(" => ");
			buffer.append(priceReached.getLowerPrice().doubleValue()).append(" ~ ").append(priceReached.getUpperPrice().doubleValue());
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	public Map<String, List<CryptoCurrencyPriceReached>> getCryptoCurrencyRegisterInfos() {
		return this.cryptoCurrencyRegisterMap;
	}

	public Map<String, List<StockPriceReached>> getStockRegisterInfos() {
		return this.stockRegisterMap;
	}
	
	public Set<String> getAllRegisteredStockNameOrId() {
		Set<String> uniqueStockNameOrId = new HashSet<>();
		
		Collection<List<StockPriceReached>> allRegisteredStockNameOrId = this.stockRegisterMap.values();
		for (List<StockPriceReached> pricesReached : allRegisteredStockNameOrId) {
			for (StockPriceReached priceReached : pricesReached) {
				uniqueStockNameOrId.add(priceReached.getStockNameOrId());
			}
		}
		
		return uniqueStockNameOrId;
	}
}
