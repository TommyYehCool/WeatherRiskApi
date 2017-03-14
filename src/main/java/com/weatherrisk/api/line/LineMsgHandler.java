package com.weatherrisk.api.line;

import java.util.Random;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.weatherrisk.api.service.BitcoinService;
import com.weatherrisk.api.service.CwbService;
import com.weatherrisk.api.service.ParkingLotService;

@LineMessageHandler
public class LineMsgHandler {
	private static final Logger logger = LoggerFactory.getLogger(LineMsgHandler.class);
	
	@Autowired
	private CwbService cwbService;
	
	@Autowired
	private ParkingLotService parkingLotService;
	
	@Autowired
	private BitcoinService bitcoinService;
	
	private final String[] templateMsgs 
		= new String[] {
			"你好呀, 吃飽沒?",
			"今天有沒有打扮得很美呀?",
			"我愛你",
			"去簽看看樂透會不會中獎"
		};
	
    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	logger.info(">>>>> handle text message event, event: {}", event);

    	String inputMsg = event.getMessage().getText();
    	
    	// 停車場_精準搜尋
    	if (inputMsg.startsWith("#")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		String queryResult = parkingLotService.findByName(name);
    		return new TextMessage(queryResult);
    	}
    	// 停車場_模糊搜尋
    	else if (inputMsg.startsWith("@")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		String queryResult = parkingLotService.findByNameLike(name);
    		return new TextMessage(queryResult);
    	}
    	// 天氣_城市小幫手
    	else if (inputMsg.endsWith("天氣")) {
    		String city = inputMsg.substring(0, inputMsg.length() - 2);
    		String queryResult = cwbService.getWeatherLitteleHelperByCity(city);
    		return new TextMessage(queryResult);
    	}
    	// 天氣_查詢一周提供資訊
    	else if (inputMsg.equals("一週提供") || inputMsg.equals("一周提供")) {
    		String queryResult = cwbService.getOneWeekWeatherPredictionProvided();
    		return new TextMessage(queryResult);
    	}
    	// 天氣_一周資訊
    	else if (!inputMsg.startsWith("桃園市") && (inputMsg.endsWith("一週") || inputMsg.endsWith("一周"))) {
    		String region = inputMsg.substring(0, inputMsg.length() - 2);
    		String queryResult = cwbService.getOneWeekWeatherPrediction(region);
    		return new TextMessage(queryResult);
    	}
    	// 天氣_桃園市某區一週資訊
    	else if (inputMsg.startsWith("桃園市") && (inputMsg.endsWith("一週") || inputMsg.endsWith("一周"))) {
    		if (!inputMsg.contains("區")) {
    			return new TextMessage("請指定某一區");
    		}
    		String taoyuanRegion = inputMsg.substring(3, inputMsg.length() - 2);
    		String queryResult = cwbService.getTaoyuanOneWeekWeatherPrediction(taoyuanRegion);
    		return new TextMessage(queryResult);
    	}
    	// BTC 價格
    	else if (inputMsg.compareToIgnoreCase("btc") == 0 || inputMsg.compareToIgnoreCase("bitcoin") == 0) {
    		String queryResult = bitcoinService.getPriceFromExchanges(CurrencyPair.BTC_USD);
    		return new TextMessage(queryResult);
    	}
    	// ETH 價格
    	else if (inputMsg.compareToIgnoreCase("eth") == 0) {
    		String queryResult = bitcoinService.getPriceFromExchanges(CurrencyPair.ETH_USD);
    		return new TextMessage(queryResult);
    	}
    	return new TextMessage(getRandomMsg());
    }
    
    private String getRandomMsg() {
    	Random random = new Random();
    	return String.valueOf(templateMsgs[random.nextInt(templateMsgs.length)]);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        logger.info(">>>>> handle default message event, event: {}", event);
    }
}
