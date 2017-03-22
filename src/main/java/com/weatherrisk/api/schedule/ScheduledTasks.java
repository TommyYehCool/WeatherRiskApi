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
import com.weatherrisk.api.service.CurrencyService;
import com.weatherrisk.api.service.RegisterService;
import com.weatherrisk.api.vo.PriceReached;

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
    	try {
    		logger.info(">>>>> Prepare to get BTC price from BTC-E...");
			BigDecimal btcLastPrice = currencyService.getCryptoLastPriceFromBtcE(CurrencyPair.BTC_USD);
			logger.info("<<<<< Get BTC price from BTC-E done, price: {}", btcLastPrice);

			checkPriceAndSendPushMessage(CurrencyCnst.BTC, CurrencyPair.BTC_USD, btcLastPrice);
			
		} catch (Exception e) {
			logger.info("Exception raised while getting BTC price from BTC-E", e);
		}
    }

    @Scheduled(cron = CRON_SCHEDULED)
    public void getETHPrice() {
    	try {
    		logger.info(">>>>> Prepare to get ETH price from BTC-E...");
			BigDecimal ethLastPrice = currencyService.getCryptoLastPriceFromBtcE(CurrencyPair.ETH_USD);
			logger.info("<<<<< Get ETH price from BTC-E done, price: {}", ethLastPrice);

			checkPriceAndSendPushMessage(CurrencyCnst.ETH, CurrencyPair.ETH_USD, ethLastPrice);
			
		} catch (Exception e) {
			logger.info("Exception raised while getting ETH price from BTC-E", e);
		}
    }
    
    @Scheduled(cron = CRON_SCHEDULED)
    public void getLTCPrice() {
    	try {
    		logger.info(">>>>> Prepare to get LTC price from BTC-E...");
			BigDecimal ltcLastPrice = currencyService.getCryptoLastPriceFromBtcE(CurrencyPair.LTC_USD);
			logger.info("<<<<< Get LTC price from BTC-E done, price: {}", ltcLastPrice);

			checkPriceAndSendPushMessage(CurrencyCnst.LTC, CurrencyPair.LTC_USD, ltcLastPrice);
			
		} catch (Exception e) {
			logger.info("Exception raised while getting LTC price from BTC-E", e);
		}
    }
    
	private void checkPriceAndSendPushMessage(CurrencyCnst baseCurrency, CurrencyPair currencyPair, BigDecimal lastPrice) {
		Map<String, List<PriceReached>> registerInfos = registerService.getRegisterInfos();
		
		String[] userIds = registerInfos.keySet().toArray(new String[0]);
		for (String userId : userIds) {
			List<PriceReached> pricesReached = registerInfos.get(userId);
			for (PriceReached priceReached : pricesReached) {
				if (priceReached.getCurrency().equals(baseCurrency)) {
					BigDecimal lowerPrice = priceReached.getLowerPrice();
					BigDecimal upperPrice = priceReached.getUpperPrice();
					if (lastPrice.doubleValue() <= lowerPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 小於 " + lowerPrice.doubleValue() + " 該買進囉!!";
						lineMessagingClient.pushMessage(new PushMessage(userId, new TextMessage(pushMsg)));
						logger.info(">>>> Send push msg: <{}> to userId: <{}> done", pushMsg, userId);
						break;
					}
					else if (lastPrice.doubleValue() >= upperPrice.doubleValue()) {
						String pushMsg = currencyPair.toString() + " 大於 " + upperPrice.doubleValue() + " 該賣出囉!!";
						lineMessagingClient.pushMessage(new PushMessage(userId, new TextMessage(pushMsg)));
						logger.info(">>>> Send push msg: <{}> to userId: <{}> done", pushMsg, userId);
						break;
					}
				}
			}
		}
	}
    
}