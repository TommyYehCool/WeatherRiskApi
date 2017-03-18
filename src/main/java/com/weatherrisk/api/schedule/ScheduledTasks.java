package com.weatherrisk.api.schedule;

import java.math.BigDecimal;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.weatherrisk.api.service.CurrencyService;

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

    @Autowired
    private CurrencyService currencyService;
    
//    @Autowired
//    private LineMessagingClient lineMessagingClient;

    @Scheduled(cron = "0 5 * * * *")
    public void getETHPrice() {
    	try {
    		logger.info(">>>>> Prepare to get ETH price from BTC-E...");
			BigDecimal ethLastPrice = currencyService.getCryptoLastPriceFromBtcE(CurrencyPair.ETH_USD);
			if (ethLastPrice.doubleValue() >= 50) {
				logger.info("~~~~~ 該賣出囉 ~~~~~");
			}
			else if (ethLastPrice.doubleValue() <= 40) {
				logger.info("~~~~~ 該買進囉 ~~~~~");
			}
			
			logger.info("<<<<< Get ETH price from BTC-E done, price: <{}>", ethLastPrice);
		} catch (Exception e) {
			logger.info("Exception raised while getting ETH price from BTC-E", e);
		}
    	
    	// 放棄, 看來要付月費才能發送 PUSH
//		lineMessagingClient.pushMessage(new PushMessage(null, new TextMessage("ETH/USD: " + ethLastPrice.doubleValue())));
    }
    
}