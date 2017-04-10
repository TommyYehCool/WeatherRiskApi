package com.weatherrisk.api.schedule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.service.currency.CurrencyService;
import com.weatherrisk.api.service.currency.RegisterService;
import com.weatherrisk.api.vo.CryptoCurrencyPriceReached;

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
	
	private final String CRON_SCHEDULED = "0 0/5 * * * ?";

    @Autowired
    private CurrencyService currencyService;
    
    @Autowired
    private RegisterService registerService;
    
    @Autowired
    private LineMessagingClient lineMessagingClient;
    
    @Scheduled(cron = CRON_SCHEDULED)
    public void getBTCPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.BTC, CurrencyPair.BTC_USD);
    }

    @Scheduled(cron = CRON_SCHEDULED)
    public void getETHPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.ETH, CurrencyPair.ETH_USD);
    }
    
    @Scheduled(cron = CRON_SCHEDULED)
    public void getLTCPrice() {
    	getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst.LTC, CurrencyPair.LTC_USD);
    }
    
    private void getCryptoCurrencyLastPriceFromBtcE(CurrencyCnst baseCurrency, CurrencyPair currencyPair) {
    	try {
    		logger.info(">>>>> Prepare to get {} price from BTC-E...", baseCurrency);

			BigDecimal lastPrice = currencyService.getCryptoLastPriceFromBtcE(currencyPair);

			logger.info("<<<<< Get {} price from BTC-E done, price: {}", baseCurrency, lastPrice);

			checkPriceAndSendPushMessage(baseCurrency, currencyPair, lastPrice);
			
		} catch (Exception e) {
			logger.info("Exception raised while getting {} price from BTC-E", baseCurrency, e);
		}
    }
    
	private void checkPriceAndSendPushMessage(CurrencyCnst baseCurrency, CurrencyPair currencyPair, BigDecimal lastPrice) {
		Map<String, List<CryptoCurrencyPriceReached>> registerInfos = registerService.getCryptoCurrencyRegisterInfos();
		
		String[] userIds = registerInfos.keySet().toArray(new String[0]);
		for (String userId : userIds) {
			List<CryptoCurrencyPriceReached> pricesReached = registerInfos.get(userId);
			for (CryptoCurrencyPriceReached priceReached : pricesReached) {
				if (priceReached.getCurrency().equals(baseCurrency)) {
					BigDecimal lowerPrice = priceReached.getLowerPrice();
					BigDecimal upperPrice = priceReached.getUpperPrice();
					if (lastPrice.doubleValue() <= lowerPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 目前價格: " + lastPrice.doubleValue() + " 小於 " + lowerPrice.doubleValue() + " 該買進囉!!";
						sendPushMessage(userId, pushMsg);
						break;
					}
					else if (lastPrice.doubleValue() >= upperPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 目前價格: " + lastPrice.doubleValue() + " 大於 " + upperPrice.doubleValue() + " 該賣出囉!!";
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