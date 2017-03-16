package com.weatherrisk.api.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.cnst.UBikeCity;
import com.weatherrisk.api.cnst.ViewshowTheater;
import com.weatherrisk.api.service.CurrencyService;
import com.weatherrisk.api.service.CwbService;
import com.weatherrisk.api.service.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.ParkingLotService;
import com.weatherrisk.api.service.TaipeiOpenDataService;
import com.weatherrisk.api.service.ViewshowMovieService;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

import lombok.NonNull;

/**
 * <pre>
 * 收到 LINE message 處理地方
 * 
 * 參考: <a href="https://github.com/line/line-bot-sdk-java">line-bot-sdk</a>
 * 
 * 參考: <a href="https://github.com/line/line-bot-sdk-java/blob/master/sample-spring-boot-kitchensink/src/main/java/com/example/bot/spring/KitchenSinkController.java">Usage example</a>
 * 
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@LineMessageHandler
public class LineMsgHandler {
	private static final Logger logger = LoggerFactory.getLogger(LineMsgHandler.class);
	
	private final int LINE_MAXIMUM_REAPLY_TEXT_MSG_LENGTH = 2000;
	private final int LINE_MAXIMUM_REAPLY_MSG_SIZE = 5;
	
	@Autowired
    private LineMessagingClient lineMessagingClient;
	
	@Autowired
	private CwbService cwbService;
	
	@Autowired
	private ParkingLotService parkingLotService;
	
	@Autowired
	private CurrencyService bitcoinService;
	
	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;
	
	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;
	
	@Autowired
	private ViewshowMovieService viewshowMovieService;
	
	private final String[] templateMsgs 
		= new String[] {
			"你好呀, 吃飽沒?",
			"今天有沒有打扮得很美呀?",
			"我愛你!",
			"去簽看看樂透會不會中獎!"
		};
	
    private String getRandomMsg() {
		Random random = new Random();
		return String.valueOf(templateMsgs[random.nextInt(templateMsgs.length)]);
	}

	@EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	logger.info(">>>>> handle text message event, event: {}", event);

    	String inputMsg = event.getMessage().getText();
    	
    	String queryResult = null;
    	
    	// 停車場_精準搜尋
    	if (inputMsg.startsWith("#")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		queryResult = parkingLotService.findByName(name);
    	}
    	// 停車場_模糊搜尋
    	else if (inputMsg.startsWith("@")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		queryResult = parkingLotService.findByNameLike(name);
    	}
    	// UBike_場站名稱模糊搜尋
    	else if (inputMsg.endsWith("ubike")) {
    		final int cityNameLen = 3;
    		
    		String cityName = inputMsg.substring(0, cityNameLen);

    		boolean isSupportedCity = UBikeCity.isSupportedCity(cityName);
    		if (isSupportedCity) {
    			String name = inputMsg.substring(inputMsg.indexOf(cityName) + cityName.length(), inputMsg.indexOf("ubike"));
    			if (name.isEmpty()) {
    				return new TextMessage("請輸入查詢關建字");
    			}

    			UBikeCity ubikeCity = UBikeCity.convertByCityName(cityName);
				switch (ubikeCity) {
	    			case TAIPEI:
	    				queryResult = taipeiOpenDataService.getNewestUBikeInfoByNameLike(name);
	    				break;

					case NEW_TAIPEI_CITY:
						queryResult = newTaipeiOpenDataService.getNewestUBikeInfoByNameLike(name);
						break;
    			}
    		}
    		else {
    			queryResult = "目前只援台北市及新北市, 搜尋範例: 台北市 + 關鍵字 + ubike";
    		}
    	}
    	// 天氣_城市小幫手
    	else if (inputMsg.endsWith("天氣")) {
    		String city = inputMsg.substring(0, inputMsg.length() - 2);
    		queryResult = cwbService.getWeatherLitteleHelperByCity(city);
    	}
    	// 天氣_一周資訊
    	else if (inputMsg.endsWith("一週") || inputMsg.endsWith("一周")) {
    		String region = inputMsg.substring(0, inputMsg.length() - 2);
    		queryResult = cwbService.getOneWeekWeatherPrediction(region);
    	}
    	// 貨幣匯率
    	else if (CurrencyCnst.isSupportedCurrency(inputMsg)) {
    		CurrencyCnst currency = CurrencyCnst.convert(inputMsg);
    		
    		// 虛擬貨幣
    		if (CurrencyCnst.isCryptoCurrency(inputMsg)) {
    			switch (currency) {
					case BTC:
						queryResult = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.BTC_USD);
						break;
						
					case ETH:
						queryResult = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.ETH_USD);
			    		break;

					default:
						break;
    			}
     		}
    		// 真實貨幣
    		else if (CurrencyCnst.isRealCurrency(inputMsg)) {
    			queryResult = bitcoinService.getRealCurrencyRatesFromTaiwanBank(currency);
    		}
    	}
    	// 威秀電影時刻
    	else if (ViewshowTheater.isSupportedTheater(inputMsg)) {
    		ViewshowTheater theather = ViewshowTheater.convertByInputMsg(inputMsg);
    		
    		String filmName = inputMsg.substring(theather.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(filmName)) {
    			queryResult = "請輸入欲查詢電影名稱";
    		}
    		else {
    			queryResult = viewshowMovieService.queryByTheaterNameAndFilmNameLike(theather.getChineseName(), filmName);
    		}
    	}
    	
    	// 回傳查詢結果
    	if (queryResult != null) {
    		if (queryResult.length() > LINE_MAXIMUM_REAPLY_TEXT_MSG_LENGTH) {
    			logger.warn("!!!!! Prepare to reply message length: <{}> excceed LINE maximum reply message length: <{}>", queryResult.length(), LINE_MAXIMUM_REAPLY_TEXT_MSG_LENGTH);
    			return new TextMessage("資料量太多(" + queryResult.length() + "), 無法回覆");
    		}
    		return new TextMessage(queryResult);
    	}
    	else {
    		return new TextMessage(getRandomMsg());
    	}
    }
	
	@EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		logger.info(">>>>> handle location message event, event: {}", event);
		
        LocationMessageContent locMsg = event.getMessage();
        
        String address = locMsg.getAddress();
        Double userLatitude = locMsg.getLatitude();
        Double userLongitude = locMsg.getLongitude();

        boolean isSupportedAddress = UBikeCity.isSupportedAddress(address);
        if (isSupportedAddress) {
        	logger.info("-----> The adderss is supported: {}", address);
        	
        	List<UBikeInfo> nearbyUbikeInfos = null;
        	
        	UBikeCity ubikeCity = UBikeCity.convertByAddress(address);
        	switch (ubikeCity) {
	        	case TAIPEI:
	        		nearbyUbikeInfos
	        			= taipeiOpenDataService.getNearbyUBikeStations(userLatitude, userLongitude);
	        		break;

				case NEW_TAIPEI_CITY:
					nearbyUbikeInfos
        				= newTaipeiOpenDataService.getNearbyUBikeStations(userLatitude, userLongitude);
					break;
        	}
        	
        	if (nearbyUbikeInfos != null && !nearbyUbikeInfos.isEmpty()) {
        		List<Message> msgs = constructLocationMessages(nearbyUbikeInfos);
        		reply(event.getReplyToken(), msgs);
        	}
        	else {
        		reply(event.getReplyToken(), new TextMessage("查詢失敗"));
        	}
        }
        else {
        	reply(event.getReplyToken(), new TextMessage("你所在位置不支援"));
        }
    }
    
    private List<Message> constructLocationMessages(List<UBikeInfo> nearbyUbikeInfos) {
    	int i = 1;
    	
    	List<Message> msgs = new ArrayList<>();
    	for (UBikeInfo ubikeInfo : nearbyUbikeInfos) {
    		String title = null;
    		if (i == 1) {
    			title = "最近的 UBike 租借站: " + ubikeInfo.getSna();
    		}
    		else {
    			title = "第" + i + "近的 UBike 租借站: " + ubikeInfo.getSna(); 
    		}
    		i++;
			String address = ubikeInfo.getAr();
			double latitude = ubikeInfo.getLat();
			double longitude = ubikeInfo.getLng();
			
			LocationMessage locMsg = new LocationMessage(title, address, latitude, longitude);
    		msgs.add(locMsg);
    		
    		String bicycleInfoMessage = constructBicycleInfoMessage(ubikeInfo);
			TextMessage textMessage = new TextMessage(bicycleInfoMessage);
			msgs.add(textMessage);
    	}
		return msgs;
	}

	private String constructBicycleInfoMessage(UBikeInfo ubikeInfo) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("總停車格: ").append(ubikeInfo.getTot()).append("\n");
		buffer.append("可租借車輛數量: ").append(ubikeInfo.getSbi()).append("\n");
		buffer.append("剩餘空位數: ").append(ubikeInfo.getBemp());
		return buffer.toString();
	}

	@EventMapping
    public void handleDefaultMessageEvent(Event event) {
        logger.info(">>>>> handle default message event, event: {}", event);
    }
    
    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
        	BotApiResponse apiResponse = null;
        	if (messages.size() > LINE_MAXIMUM_REAPLY_MSG_SIZE) {
        		List<Message> errorMsg = Arrays.asList(new TextMessage[] {new TextMessage("資料數目超過可回傳訊息")});
        		
        		apiResponse 
            		= lineMessagingClient
                    	.replyMessage(new ReplyMessage(replyToken, errorMsg))
                    	.get();
        	}
        	else {
	            apiResponse 
	            	= lineMessagingClient
	                    .replyMessage(new ReplyMessage(replyToken, messages))
	                    .get();
        	}
            logger.info("Sent messages: {}", apiResponse);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Expcetion raised while tring to reply", e);
        }
    }
}
