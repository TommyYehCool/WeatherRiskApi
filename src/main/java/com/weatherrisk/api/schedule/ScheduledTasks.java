package com.weatherrisk.api.schedule;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.weatherrisk.api.cnst.currency.CurrencyCnst;
import com.weatherrisk.api.service.currency.CurrencyService;
import com.weatherrisk.api.service.currency.RegisterService;
import com.weatherrisk.api.service.stock.StockService;
import com.weatherrisk.api.vo.CryptoCurrencyPriceReached;
import com.weatherrisk.api.vo.StockPriceReached;

/**
 * <pre>
 * Spring Boot 內建排程
 * 
 * 參考:
 * <a href="https://spring.io/guides/gs/scheduling-tasks/">Spring Boot 內建排程</a>
 * <a href="http://stackoverflow.com/questions/26147044/spring-cron-expression-for-every-day-101am">Cron Expression</a>
 * 
 * Cron Expression 格式說明:
 * 	second, minute, hour, day of month, month, day(s) of week
 *  
 * Heroku 筆記: 
 *	放在 heroku 上, 記得要設定 heroku timezone
 *
 *	指令: 
 *		heroku config:add TZ="Asia/Taipei" --app tommywebservice
 * </pre>
 * 
 * @author tommy.feng
 */
@Component
public class ScheduledTasks {
	
	private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	
	private final String GET_BTCE_CRON_EXP = "0 0/5 * * * ?";
	
	private final String GET_POLONEIX_CRON_EXP = "0/10 * * * * ?";
	
	private final String GET_STOCK_CRON_EXP = "0 0/5 * * * ?";

    @Autowired
    private CurrencyService currencyService;
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private RegisterService registerService;
    
    @Autowired
    private LineMessagingClient lineMessagingClient;
    
    @Scheduled(cron = GET_BTCE_CRON_EXP)
    public void getBTCPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.BTC, CurrencyPair.BTC_USD);
    }

    @Scheduled(cron = GET_BTCE_CRON_EXP)
    public void getETHPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.ETH, CurrencyPair.ETH_USD);
    }
    
    @Scheduled(cron = GET_BTCE_CRON_EXP)
    public void getLTCPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.LTC, CurrencyPair.LTC_USD);
    }
    
    @Scheduled(cron = GET_POLONEIX_CRON_EXP)
    public void getSTRPrice() {
    	getCryptoCurrencyLastPriceFromPoloneix(CurrencyCnst.STR, CurrencyPair.STR_BTC);
    }
    
    @Scheduled(cron = GET_POLONEIX_CRON_EXP)
    public void getXRPPrice() {
    	getCryptoCurrencyLastPriceFromPoloneix(CurrencyCnst.XRP, CurrencyPair.XRP_BTC);
    }
    
    @Scheduled(cron = GET_STOCK_CRON_EXP)
    public void getRegisteredStockPrice() {
    	Set<String> allRegisteredStockNameOrId = registerService.getAllRegisteredStockNameOrId();
    	Iterator<String> it = allRegisteredStockNameOrId.iterator();
    	while (it.hasNext()) {
    		String stockNameOrId = it.next();
    		try {
	    		logger.debug(">>>>> Prepare to get stock match price with stockNameOrId: <{}>", stockNameOrId);
	    		
	    		BigDecimal matchPrice = stockService.getStockMatchPriceByNameOrId(stockNameOrId);
	    		
	    		logger.debug("<<<<< Get stock match price with stockNameOrId: <{}> done, match price: {}", stockNameOrId, matchPrice);
	    		
	    		checkStockPriceAndSendPushMessage(stockNameOrId, matchPrice);
    		} catch (Exception e) {
    			logger.error("Exception raised while trying to get stock match price with stockNameOrId: <{}>", stockNameOrId, e);
    		}
    	}
    }
    
	private void getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst baseCurrency, CurrencyPair currencyPair) {
    	try {
    		logger.debug(">>>>> Prepare to get {} price from BTC-E...", baseCurrency);

			BigDecimal lastPrice = currencyService.getCryptoLastPriceFromBtcE(currencyPair);

			logger.debug("<<<<< Get {} price from BTC-E done, price: {}", baseCurrency, lastPrice);

			checkCryptoCurrencyPriceAndSendPushMessage(baseCurrency, currencyPair, lastPrice);
		} catch (Exception e) {
			logger.error("Exception raised while trying to get {} price from BTC-E", baseCurrency, e);
		}
    }
	
	private void getCryptoCurrencyLastPriceFromPoloneix(CurrencyCnst baseCurrency, CurrencyPair currencyPair) {
    	try {
    		logger.debug(">>>>> Prepare to get {} price from Poloneix...", baseCurrency);

			BigDecimal lastPrice = currencyService.getCryptoLastPriceFromPoloneix(currencyPair);

			logger.debug("<<<<< Get {} price from Poloneix done, price: {}", baseCurrency, lastPrice);

			checkCryptoCurrencyPriceAndSendPushMessage(baseCurrency, currencyPair, lastPrice);
		} catch (Exception e) {
			logger.error("Exception raised while trying to get {} price from BTC-E", baseCurrency, e);
		}
	}
    
	private void checkCryptoCurrencyPriceAndSendPushMessage(CurrencyCnst baseCurrency, CurrencyPair currencyPair, BigDecimal lastPrice) {
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		
		Map<String, List<CryptoCurrencyPriceReached>> registerInfos = registerService.getCryptoCurrencyRegisterInfos();
		
		String[] userIds = registerInfos.keySet().toArray(new String[0]);
		for (String userId : userIds) {
			List<CryptoCurrencyPriceReached> pricesReached = registerInfos.get(userId);
			for (CryptoCurrencyPriceReached priceReached : pricesReached) {
				if (priceReached.getCurrency().equals(baseCurrency)) {
					BigDecimal lowerPrice = priceReached.getLowerPrice();
					BigDecimal upperPrice = priceReached.getUpperPrice();
					if (lastPrice.doubleValue() <= lowerPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 目前價格: " + decFormat.format(lastPrice.doubleValue()) + " 小於 " + decFormat.format(lowerPrice.doubleValue()) + " 該買進囉!!";
						sendPushMessage(userId, pushMsg);
						break;
					}
					else if (lastPrice.doubleValue() >= upperPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 目前價格: " + decFormat.format(lastPrice.doubleValue()) + " 大於 " + decFormat.format(upperPrice.doubleValue()) + " 該賣出囉!!";
						sendPushMessage(userId, pushMsg);
						break;
					}
				}
			}
		}
	}
	
	private void checkStockPriceAndSendPushMessage(String stockNameOrId, BigDecimal matchPrice) {
		Map<String, List<StockPriceReached>> registerInfos = registerService.getStockRegisterInfos();
		
		String[] userIds = registerInfos.keySet().toArray(new String[0]);
		for (String userId : userIds) {
			List<StockPriceReached> pricesReached = registerInfos.get(userId);
			for (StockPriceReached priceReached : pricesReached) {
				if (priceReached.getStockNameOrId().equals(stockNameOrId)) {
					BigDecimal lowerPrice = priceReached.getLowerPrice();
					BigDecimal upperPrice = priceReached.getUpperPrice();
					if (matchPrice.doubleValue() <= lowerPrice.doubleValue()) {
						String pushMsg = stockNameOrId + " 目前成交價: " + matchPrice.doubleValue() + " 小於 " + lowerPrice.doubleValue();
						sendPushMessage(userId, pushMsg);
						break;
					}
					else if (matchPrice.doubleValue() >= upperPrice.doubleValue()) {
						String pushMsg = stockNameOrId + " 目前成交價: " + matchPrice.doubleValue() + " 大於 " + upperPrice.doubleValue() + " 該賣出囉!!";
						sendPushMessage(userId, pushMsg);
						break;
					}
				}
			}
		}
	}

	private void sendPushMessage(String userId, String pushMsg) {
		lineMessagingClient.pushMessage(new PushMessage(userId, new TextMessage(pushMsg)));
		logger.info(">>>> Send push msg: <{}> to userId: <{}> done", pushMsg, userId);
	}
    
}