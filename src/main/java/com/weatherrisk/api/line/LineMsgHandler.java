package com.weatherrisk.api.line;

import java.math.BigDecimal;
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
import com.weatherrisk.api.cnst.AmbassadorTheater;
import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.cnst.MiramarTheater;
import com.weatherrisk.api.cnst.ShowTimeTheater;
import com.weatherrisk.api.cnst.UBikeCity;
import com.weatherrisk.api.cnst.ViewshowTheater;
import com.weatherrisk.api.cnst.WovieTheater;
import com.weatherrisk.api.service.currency.CurrencyService;
import com.weatherrisk.api.service.currency.RegisterService;
import com.weatherrisk.api.service.movie.AmbassadorMovieService;
import com.weatherrisk.api.service.movie.MiramarMovieService;
import com.weatherrisk.api.service.movie.ShowTimeMovieService;
import com.weatherrisk.api.service.movie.ViewshowMovieService;
import com.weatherrisk.api.service.movie.WovieMovieService;
import com.weatherrisk.api.service.opendata.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.opendata.TaipeiOpenDataService;
import com.weatherrisk.api.service.parkinglot.ParkingLotService;
import com.weatherrisk.api.service.receiptreward.ReceiptRewardService;
import com.weatherrisk.api.service.weather.CwbService;
import com.weatherrisk.api.vo.PriceReached;
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
	
	private final int LINE_MAXIMUM_REPLY_TEXT_MSG_LENGTH = 2000;
	private final int LINE_MAXIMUM_REPLY_MSG_SIZE = 5;
	
	@Autowired
    private LineMessagingClient lineMessagingClient;
	
	@Autowired
	private ParkingLotService parkingLotService;
	
	@Autowired
	private CwbService cwbService;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private RegisterService registerService;
	
	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;
	
	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;
	
	@Autowired
	private ViewshowMovieService viewshowMovieService;
	
	@Autowired
	private ShowTimeMovieService showTimeMovieService;
	
	@Autowired
	private MiramarMovieService miramarMovieService;
	
	@Autowired
	private WovieMovieService wovieMovieService;
	
	@Autowired
	private AmbassadorMovieService ambassadorMovieService;
	
	@Autowired
	private ReceiptRewardService receiptRewardService;
	
	private final String[] helpTemplateMsgs
		= new String[] {
				"支援功能",
				"幹嘛",
				"What can you do"
		  };
	
	private final String[] templateMsgs 
		= new String[] {
				"你覺得今天天氣如何呀?",
				"看你運勢不錯去簽一張樂透試試手氣如何",
				"工作不順嗎, 喝罐咖啡提提神",
				"假日快到了, 有沒有安排去哪走走呢?"
		  };
	
    private String getRandomMsg() {
		Random random = new Random();
		return String.valueOf(templateMsgs[random.nextInt(templateMsgs.length)]);
	}
    
    private String constructHelpMsg() {
    	StringBuilder buffer = new StringBuilder();

    	buffer.append("我能做到下列事情:").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[查詢台北市及新北市停車場資訊]").append("\n");
    	buffer.append("模糊搜尋 => Ex: @士林").append("\n");
    	buffer.append("\n");
    	buffer.append("停車場名稱搜尋 => Ex: #停車場名稱").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[查詢天氣]").append("\n");
    	buffer.append("查詢天氣小幫手 => 格式: 縣市名稱 + 天氣").append("\n");
    	buffer.append("Ex: 台北市天氣").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢一周天氣 => 格式: 縣市名稱 + 一周").append("\n");
    	buffer.append("Ex: 台北市一周").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[查詢貨幣匯率]").append("\n");
    	buffer.append("<支援虛擬貨幣: btc, eth, ltc>").append("\n");
    	buffer.append("<支援真實貨幣: usd, jpy...等>").append("\n");
    	buffer.append("查詢虛擬貨幣匯率 => Ex: btc, eth, ltc").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢真實貨幣匯率 => Ex: usd, jpy...等").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[註冊虛擬貨幣到價通知]").append("\n");
    	buffer.append("註冊虛擬貨幣到價通知 => Ex: 註冊eth 40 50").append("\n");
    	buffer.append("\n");
    	buffer.append("取消虛擬貨幣到價通知 => Ex: 取消eth").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢註冊虛擬貨幣到價通知資訊 => Ex: 查詢註冊").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[UBike]").append("\n");
    	buffer.append("關鍵字查詢 => 格式: 縣市名稱 + 關鍵字 + ubike").append("\n");
    	buffer.append("Ex: 台北市天母ubike, 新北市三重ubike").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢最近的兩個 UBike 場站資訊 => 傳送您目前的位置資訊即可").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[電影]").append("\n");
    	buffer.append("<支援威秀影城: 信義威秀, 京站威秀, 日新威秀, 板橋大遠百威秀>").append("\n");
    	buffer.append("<支援秀泰影城: 欣欣秀泰, 今日秀泰, 板橋秀泰, 東南亞秀泰>").append("\n");
    	buffer.append("<支援美麗華影城: 大直美麗華>").append("\n");
    	buffer.append("<支援華威影城: 天母華威>").append("\n");
    	buffer.append("<支援國賓影城: 西門國賓, 微風國賓, 晶冠國賓>").append("\n");
    	buffer.append("請系統更新電影時刻表 => Ex: 更新電影時刻表").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢某一家影城上映電影 => 格式: 戲院名稱 + 上映").append("\n");
    	buffer.append("Ex: 信義威秀上映").append("\n");
    	buffer.append("\n");
    	buffer.append("查詢某一部電影今日時刻表 => 格式: 戲院名稱 + 關鍵字").append("\n");
    	buffer.append("Ex: 信義威秀羅根").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[發票]").append("\n");
    	buffer.append("更新發票開獎號碼 => Ex: 更新發票").append("\n");
    	buffer.append("查詢最近兩期發票開獎號碼 => Ex: 發票開獎").append("\n");
    	buffer.append("發票對獎功能, 直接輸入號碼即可 => Ex: 168").append("\n");
    	
    	return buffer.toString();
	}

	private boolean isQueryFunctionMsg(String inputMsg) { 
    	for (String helpTemplateMsg : helpTemplateMsgs) {
    		if (inputMsg.contains(helpTemplateMsg)) {
    			return true;
    		}
    	}
    	return false;
    }

	@EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	logger.info(">>>>> handle text message event, event: {}", event);
    	
    	String userId = event.getSource().getUserId();

    	String inputMsg = event.getMessage().getText();
    	
    	String queryResult = null;
    	
    	// 功能查詢
    	if (isQueryFunctionMsg(inputMsg)) {
    		queryResult = constructHelpMsg();
    	}
    	// 停車場-精準搜尋
    	else if (inputMsg.startsWith("#")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		queryResult = parkingLotService.findByName(name);
    	}
    	// 停車場-模糊搜尋
    	else if (inputMsg.startsWith("@")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		queryResult = parkingLotService.findByNameLike(name);
    	}
    	// 天氣-城市小幫手
    	else if (inputMsg.endsWith("天氣")) {
    		String city = inputMsg.substring(0, inputMsg.length() - 2);
    		queryResult = cwbService.getWeatherLitteleHelperByCity(city);
    	}
    	// 天氣-一周資訊
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
						queryResult = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.BTC_USD);
						break;
						
					case ETH:
						queryResult = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.ETH_USD);
			    		break;
			    		
					case LTC:
						queryResult = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.LTC_USD);
						break;
						
					default:
						break;
    			}
     		}
    		// 真實貨幣
    		else if (CurrencyCnst.isRealCurrency(inputMsg)) {
    			queryResult = currencyService.getRealCurrencyRatesFromTaiwanBank(currency);
    		}
    	}
    	// 註冊虛擬貨幣匯率到價通知
    	else if (inputMsg.startsWith("註冊")) {
    		String cryptoCurrencyAndPrice = inputMsg.substring(inputMsg.indexOf("註冊") + "註冊".length(), inputMsg.length()).trim();
    		String[] split = cryptoCurrencyAndPrice.split(" ");
    		if (split.length != 3) {
    			queryResult = "格式範例 => Ex: 註冊eth 40 50";
    		}
    		else {
    			String code = split[0].trim();
    			boolean isCryptoCurrency = CurrencyCnst.isCryptoCurrency(code);
    			if (!isCryptoCurrency) {
    				queryResult = "目前只支援 BTC, ETH, LTC; 格式範例 => Ex: 註冊eth 40 50";
    			}
    			else {
    				try {
    					CurrencyCnst currency = CurrencyCnst.convert(code);
    					BigDecimal lowerPrice = new BigDecimal(Double.parseDouble(split[1]));
    					BigDecimal upperPrice = new BigDecimal(Double.parseDouble(split[2]));
    					PriceReached priceReached = new PriceReached(currency, lowerPrice, upperPrice);
						registerService.register(userId, priceReached);
						queryResult = "註冊 " + currency + " 到價通知成功, 價格: " + lowerPrice.doubleValue() + " ~ " + upperPrice.doubleValue();
    				}
    				catch (Exception e) {
    					queryResult = "格式範例 => Ex: 註冊eth 40 50"; 
    				}
    			}
    		}
    	}
    	// 取消虛擬貨幣匯率到價通知
    	else if (inputMsg.startsWith("取消")) {
    		String cryptoCurrency = inputMsg.substring(inputMsg.indexOf("取消") + "取消".length(), inputMsg.length()).trim();
    		boolean isCryptoCurrency = CurrencyCnst.isCryptoCurrency(cryptoCurrency);
			if (!isCryptoCurrency) {
				queryResult = "目前只支援 BTC, ETH, LTC; 格式範例 => 取消btc";
			}
			else {
				CurrencyCnst currency = CurrencyCnst.convert(cryptoCurrency);
				boolean hasRegistered = registerService.hasRegistered(userId, currency);
				if (hasRegistered) {
					registerService.unregister(userId, currency);
					queryResult = "取消註冊 " + currency + " 成功";
				}
				else {
					queryResult = "您未註冊 " + currency + " 到價通知";
				}
			}
    	}
    	// 查詢註冊虛擬貨幣匯率到價通知
    	else if (inputMsg.equals("查詢註冊")) {
    		boolean hasRegistered = registerService.hasRegistered(userId);
    		if (hasRegistered) {
    			queryResult = registerService.getPricesReachedInfos(userId);
    		}
    		else {
    			queryResult = "您未註冊任何到價通知";
    		}
    	}
    	// UBike 場站名稱模糊搜尋
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
    	// 威秀電影
    	else if (ViewshowTheater.isSupportedTheater(inputMsg)) {
    		ViewshowTheater theater = ViewshowTheater.convertByInputMsg(inputMsg);
    		
    		String command = inputMsg.substring(theater.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(command)) {
    			queryResult = "請輸入欲查詢電影名稱或'上映'";
    		}
    		else if (command.equals("上映")) {
    			queryResult = viewshowMovieService.queryNowPlayingByTheaterName(theater.getChineseName());
    		}
    		else {
    			String filmName = command;
    			queryResult = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theater.getChineseName(), filmName);
    		}
    	}
    	// 秀泰電影
    	else if (ShowTimeTheater.isSupportedTheater(inputMsg)) {
    		ShowTimeTheater theater = ShowTimeTheater.convertByInputMsg(inputMsg);
    		
    		String command = inputMsg.substring(theater.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(command)) {
    			queryResult = "請輸入欲查詢電影名稱或'上映'";
    		}
    		else if (command.equals("上映")) {
    			queryResult = showTimeMovieService.queryNowPlayingByTheaterName(theater.getChineseName());
    		}
    		else {
    			String filmName = command;
    			queryResult = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theater.getChineseName(), filmName);
    		}
    	}
    	// 美麗華電影
    	else if (MiramarTheater.isSupportedTheater(inputMsg)) {
    		MiramarTheater theater = MiramarTheater.convertByInputMsg(inputMsg);
    		
    		String command = inputMsg.substring(theater.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(command)) {
    			queryResult = "請輸入欲查詢電影名稱或'上映'";
    		}
    		else if (command.equals("上映")) {
    			queryResult = miramarMovieService.queryNowPlayingByTheaterName(theater.getChineseName());
    		}
    		else {
    			String filmName = command;
    			queryResult = miramarMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theater.getChineseName(), filmName);
    		}
    	}
    	// 華威電影
    	else if (WovieTheater.isSupportedTheater(inputMsg)) {
    		WovieTheater theater = WovieTheater.convertByInputMsg(inputMsg);
    		
    		String command = inputMsg.substring(theater.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(command)) {
    			queryResult = "請輸入欲查詢電影名稱或'上映'";
    		}
    		else if (command.equals("上映")) {
    			queryResult = wovieMovieService.queryNowPlayingByTheaterName(theater.getChineseName());
    		}
    		else {
    			String filmName = command;
    			queryResult = wovieMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theater.getChineseName(), filmName);
    		}
    	}
    	// 國賓電影
    	else if (AmbassadorTheater.isSupportedTheater(inputMsg)) {
    		AmbassadorTheater theater = AmbassadorTheater.convertByInputMsg(inputMsg);
    		
    		String command = inputMsg.substring(theater.getChineseName().length(), inputMsg.length()).trim();
    		
    		if (StringUtils.isEmpty(command)) {
    			queryResult = "請輸入欲查詢電影名稱或'上映'";
    		}
    		else if (command.equals("上映")) {
    			queryResult = ambassadorMovieService.queryNowPlayingByTheaterName(theater.getChineseName());
    		}
    		else {
    			String filmName = command;
    			queryResult = ambassadorMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theater.getChineseName(), filmName);
    		} 
    	}
    	// 更新電影時刻
    	else if (inputMsg.equals("更新電影時刻表")) {
    		viewshowMovieService.refreshMovieTimes();
    		showTimeMovieService.refreshMovieTimes();
    		miramarMovieService.refreshMovieTimes();
    		wovieMovieService.refreshMovieTimes();
    		ambassadorMovieService.refreshMovieTimes();
    		queryResult = "更新成功";
    	}
    	// 更新發票
    	else if (inputMsg.equals("更新發票")) {
    		receiptRewardService.getNewestReceiptRewards();
    		queryResult = "更新成功";
    	}
    	// 發票開獎
    	else if (inputMsg.equals("發票開獎")) {
    		queryResult = receiptRewardService.getRecentlyRewards();
    	}
    	// 其他判斷
    	else {
    		// 若可轉換為數字
    		try {
    			Integer.parseInt(inputMsg);
    			
    			// 進行發票對獎
    			queryResult = receiptRewardService.checkIsBingo(inputMsg);
    		}
    		catch (Exception e) {}
    	}
    	
    	// ----- 回傳查詢結果 -----
    	if (queryResult != null) {
    		if (queryResult.length() > LINE_MAXIMUM_REPLY_TEXT_MSG_LENGTH) {
    			logger.warn("!!!!! Prepare to reply message length: <{}> exceed LINE maximum reply message length: <{}>", queryResult.length(), LINE_MAXIMUM_REPLY_TEXT_MSG_LENGTH);
    			queryResult = queryResult.substring(0, LINE_MAXIMUM_REPLY_TEXT_MSG_LENGTH - 3) + "...";
    			return new TextMessage(queryResult);
    		}
    		return new TextMessage(queryResult);
    	}
    	// ----- 不支援的指令, 回傳灌頭訊息 -----
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
		buffer.append("可借車輛: ").append(ubikeInfo.getSbi()).append("\n");
		buffer.append("可停空位: ").append(ubikeInfo.getBemp());
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
        	if (messages.size() > LINE_MAXIMUM_REPLY_MSG_SIZE) {
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
            logger.error("Exception raised while tring to reply", e);
        }
    }
}
