package com.weatherrisk.api.line;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.weatherrisk.api.cnst.currency.CurrencyCnst;
import com.weatherrisk.api.cnst.line.main.LineFinancialFunction;
import com.weatherrisk.api.cnst.line.main.LineFunction;
import com.weatherrisk.api.cnst.line.main.LineQueryFunction;
import com.weatherrisk.api.cnst.line.sub.LineSubFunction;
import com.weatherrisk.api.cnst.line.sub.financial.CryptoCurrencySubFunction;
import com.weatherrisk.api.cnst.line.sub.financial.StockSubFunction;
import com.weatherrisk.api.cnst.line.sub.query.MovieSubFunction;
import com.weatherrisk.api.cnst.line.sub.query.ParkingLotSubFunction;
import com.weatherrisk.api.cnst.line.sub.query.ReceiptRewardSubFunction;
import com.weatherrisk.api.cnst.line.sub.query.WeatherSubFunction;
import com.weatherrisk.api.cnst.movie.MovieTheater;
import com.weatherrisk.api.cnst.movie.SupprotedTheaterCompany;
import com.weatherrisk.api.cnst.movie.theaters.AmbassadorTheater;
import com.weatherrisk.api.cnst.movie.theaters.MiramarTheater;
import com.weatherrisk.api.cnst.movie.theaters.ShowTimeTheater;
import com.weatherrisk.api.cnst.movie.theaters.ViewshowTheater;
import com.weatherrisk.api.cnst.ubike.UBikeCity;
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
import com.weatherrisk.api.service.stock.StockService;
import com.weatherrisk.api.service.weather.CwbService;
import com.weatherrisk.api.vo.CryptoCurrencyPriceReached;
import com.weatherrisk.api.vo.StockPriceReached;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
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

	private final Logger logger = LoggerFactory.getLogger(LineMsgHandler.class);
	
	private final int LINE_MAXIMUM_REPLY_TEXT_MSG_LENGTH = 2000;
	private final int LINE_MAXIMUM_REPLY_MSG_SIZE = 5;
	private final int LINE_TEMPLATE_MSG_MAX_ITEMS = 4;
	
	private static final String ERROR_MSG = "系統怪怪的, 請通知管理員";

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
	
	@Autowired
	private StockService stockService;
	/**
	 * 紀錄使用者目前選擇到的功能
	 */
	private Map<String, CurrentFunction> userCurrentFunc = Collections.synchronizedMap(new HashMap<>());
	
	@Data
	@AllArgsConstructor
	private class CurrentFunction {
		private LineQueryFunction lineQueryFunc;
		private LineFinancialFunction lineFincFunc;
		private LineSubFunction lineSubFunc;
		private QueryMovieInfo queryMovieInfo;
	}
	
	@Data
	@AllArgsConstructor
	private class QueryMovieInfo {
		private SupprotedTheaterCompany theaterCompany;
		private MovieTheater movieTheater;
	}
	
	private final String[] openQueryFunctionKeywords
		= new String[] {
				"1",
				"query",
				"查詢"
		  };
	
	private final String[] openFinancialFunctionKeywords 
		 = new String[] {
				"2",
				"$",
				"finc",
				"金融"
		   };
	
	private final String[] helpMsgs
		= new String[] {
				"支援功能",
				"幹嘛",
				"What can you do"
		  };
	
	private final String[] randomRespMsgs 
		= new String[] {
				"你覺得今天天氣如何呀?",
				"看你運勢不錯去簽一張樂透試試手氣如何",
				"工作不順嗎, 喝罐咖啡提提神",
				"假日快到了, 有沒有安排去哪走走呢?"
		  };
	
    private String getRandomResponseMsg() {
		Random random = new Random();
		return String.valueOf(randomRespMsgs[random.nextInt(randomRespMsgs.length)]);
	}
    
    private String constructHelpMsg() {
    	final String supportedCryptoCurrency = CurrencyCnst.getSupportedCryptoCurrency();
    	
    	StringBuilder buffer = new StringBuilder();

    	buffer.append("1: 查詢功能").append("\n");
    	buffer.append("2: 金融功能").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[發票]").append("\n");
    	buffer.append("發票對獎功能, 直接輸入號碼即可 => Ex: 168").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[UBike]").append("\n");
    	buffer.append("關鍵字查詢 => 格式: 縣市名稱 + 關鍵字 + ubike, Ex: 台北市天母ubike, 新北市三重ubike").append("\n");
    	buffer.append("查詢最近的兩個 UBike 場站資訊 => 傳送您目前的位置資訊即可").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[貨幣]").append("\n");
		buffer.append("<支援虛擬貨幣: ").append(supportedCryptoCurrency).append(">\n");
    	buffer.append("<支援真實貨幣: usd, jpy...等>").append("\n");
    	buffer.append("查詢虛擬貨幣匯率 => Ex: ").append(supportedCryptoCurrency).append("\n");
    	buffer.append("查詢真實貨幣匯率 => Ex: usd, jpy...等").append("\n");
    	buffer.append("註冊虛擬貨幣到價通知 => Ex: 註冊貨幣eth 40 50").append("\n");
    	buffer.append("取消虛擬貨幣到價通知 => Ex: 取消貨幣eth").append("\n");
    	buffer.append("新增虛擬貨幣買進資訊 => Ex: 2017/05/08-08:07:30 買貨幣 STR 0.00004900 20000 0.15").append("\n");
    	buffer.append("新增虛擬貨幣賣出資訊 => Ex: 2017/05/08-20:07:30 賣貨幣 STR 0.00004900 20000 0.25").append("\n");
    	buffer.append("-----------------------").append("\n");
    	buffer.append("[查詢股票]").append("\n");
    	buffer.append("註冊股票到價通知 => Ex: 註冊股票 3088 40 50").append("\n");
    	buffer.append("取消股票到價通知 => Ex: 取消股票 3088").append("\n");
    	buffer.append("新增股票買進資訊 => Ex: 2017/3/24 買股票 3088 56.8 2000").append("\n");
    	buffer.append("新增股票賣出資訊 => Ex: 2017/3/24 賣股票 3088 60 2000").append("\n");
    	buffer.append("刪除股票庫存 => Ex: 刪除股票庫存鴻海").append("\n");
    	
    	return buffer.toString();
	}

	private boolean isHelpMsg(String inputMsg) { 
    	for (String helpMsg : helpMsgs) {
    		if (inputMsg.contains(helpMsg)) {
    			return true;
    		}
    	}
    	return false;
    }
	
	private boolean isOpenQueryFunctionMsg(String inputMsg) {
		for (String openQueryFunctionKeyword : openQueryFunctionKeywords) {
			if (openQueryFunctionKeyword.contains(inputMsg)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isOpenFinancialFunctionMsg(String inputMsg) {
		for (String openFinancialFunctionKeyword : openFinancialFunctionKeywords) {
			if (openFinancialFunctionKeyword.contains(inputMsg)) {
				return true;
			}
		}
		return false;
	}
 
	@EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	logger.info(">>>>> handle text message event, event: {}", event);
    	
    	String userId = event.getSource().getUserId();

    	String replyToken = event.getReplyToken();

    	String inputMsg = event.getMessage().getText();
    	
    	String queryResult = null;
    	
    	// 查詢功能表
    	if (isOpenQueryFunctionMsg(inputMsg)) {
    		createQueryFuncTemplateMsg(replyToken);
    		queryResult = "";
    	}
    	
    	// 金融功能表
    	if (isOpenFinancialFunctionMsg(inputMsg)) {
    		createFinancialFuncTemplateMsg(replyToken);
    		queryResult = "";
    	}
    	
    	// 子功能表
    	LineQueryFunction lineQryFunc = LineQueryFunction.convertByKeyword(inputMsg);
    	LineFinancialFunction lineFincFunc = LineFinancialFunction.convertByKeyword(inputMsg);
    	if (lineQryFunc != null || lineFincFunc != null) {
    		LineFunction lineFunc = null;
    		if (lineQryFunc != null) {
    			lineFunc = lineQryFunc;
    		}
    		else if (lineFincFunc != null) {
    			lineFunc = lineFincFunc;
    		}
    		createSubFuncTemplateMsg(lineFunc, replyToken);
    		queryResult = "";
    	}
    	
    	// 處理使用者目前在進行的子功能
    	CurrentFunction currentFunction = getUserCurrentFunction(userId);
    	if (currentFunction != null) {
    		if (currentFunction.getLineQueryFunc() != null) {
    			processUserCurrentQryFunction(userId, replyToken, currentFunction, inputMsg);
    		}
    		else if (currentFunction.getLineFincFunc() != null) {
    			processUserCurrentFincFunction(userId, replyToken, currentFunction, inputMsg);
    		}
    		queryResult = "";
    	}
    	
    	// 功能查詢
    	if (isHelpMsg(inputMsg)) {
    		queryResult = constructHelpMsg();
    	}
    	// 註冊股票到價通知
    	else if (inputMsg.startsWith("註冊股票")) {
    		String stockNameOrIdAndPrice = inputMsg.substring(inputMsg.indexOf("註冊股票") + "註冊股票".length(), inputMsg.length()).trim();
    		String[] split = stockNameOrIdAndPrice.split(" ");
    		if (split.length != 3) {
    			queryResult = "格式範例 => 註冊股票 艾訊 60 65";
    		}
    		else {
    			String stockNameOrId = split[0].trim();
    			boolean isSupportedStock = stockService.isSupportedStock(stockNameOrId);
    			if (!isSupportedStock) {
    				queryResult = "你輸入的商品不支援 (" + stockNameOrId + ")";
    			}
    			else {
    				try {
	    				BigDecimal lowerPrice = new BigDecimal(split[1]);
						BigDecimal upperPrice = new BigDecimal(split[2]);
						StockPriceReached stockPriceReached = new StockPriceReached(stockNameOrId, lowerPrice, upperPrice);
						registerService.registerStock(userId, stockPriceReached);
						queryResult = "註冊 " + stockNameOrId + " 到價通知成功, 價格: " + lowerPrice.doubleValue() + " ~ " + upperPrice.doubleValue();
    				}
    				catch (Exception e) {
    					queryResult = "格式範例 => 註冊股票 艾訊 60 65"; 
    				}
    			}
    		}
    	}
    	// 取消股票到價通知
    	else if (inputMsg.startsWith("取消股票")) {
    		String stockNameOrId = inputMsg.substring(inputMsg.indexOf("取消股票") + "取消股票".length(), inputMsg.length()).trim();
    		boolean isSupportedStock = stockService.isSupportedStock(stockNameOrId);
			if (!isSupportedStock) {
				queryResult = "你輸入的商品不支援 (" + stockNameOrId + ")";
			}
			else {
				boolean hasRegistered = registerService.hasRegisteredStock(userId, stockNameOrId);
				if (hasRegistered) {
					registerService.unregisterStockPrice(userId, stockNameOrId);
					queryResult = "取消註冊 " + stockNameOrId + " 成功";
				}
				else {
					queryResult = "您未註冊 " + stockNameOrId + " 到價通知";
				}
			}
    	}
    	// 新增股票買賣紀錄
    	else if (inputMsg.contains("買股票") || inputMsg.contains("賣股票")) {
    		if (inputMsg.contains("買")) {
    			String errorMsg = checkBuySellStockMsg("買股票", inputMsg);
    			if (errorMsg != null) {
    				queryResult = errorMsg;
    			}
    			else {
    				// 新增股票買資訊
    				String[] split = inputMsg.split(" ");
    				String buyDate = split[0];
					String stockNameOrId = split[2];
					double buyPrice = Double.parseDouble(split[3]);
					long buyShares = Long.parseLong(split[4]);
					queryResult = stockService.addBuyStock(userId, buyDate, stockNameOrId, buyPrice, buyShares);
    			}
    		}
    		else if (inputMsg.contains("賣")) {
    			String errorMsg = checkBuySellStockMsg("賣股票", inputMsg);
    			if (errorMsg != null) {
    				queryResult = errorMsg;
    			}
    			else {
    				// 新增股票賣資訊
    				String[] split = inputMsg.split(" ");
    				String sellDate = split[0];
					String stockNameOrId = split[2];
					double sellPrice = Double.parseDouble(split[3]);
					long sellShares = Long.parseLong(split[4]);
					queryResult = stockService.addSellStock(userId, sellDate, stockNameOrId, sellPrice, sellShares);
    			}
    		}
    		else {
    			queryResult = "格式錯誤, Ex: 2017/3/24 買股票 3088 56.8 2000";
    		}
    	}
    	// 刪除股票庫存
    	else if (inputMsg.startsWith("刪除股票庫存")) {
    		String stockNameOrId = inputMsg.substring(inputMsg.indexOf("刪除股票庫存") + "刪除股票庫存".length(), inputMsg.length()).trim();
    		queryResult = stockService.deleteTreasuryStock(userId, stockNameOrId);
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
						
					case STR:
						queryResult = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.STR_BTC);
						break;
						
					case XRP:
						queryResult = currencyService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.XRP_BTC);
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
    	else if (inputMsg.startsWith("註冊貨幣")) {
    		String cryptoCurrencyAndPrice = inputMsg.substring(inputMsg.indexOf("註冊貨幣") + "註冊貨幣".length(), inputMsg.length()).trim();
    		String[] split = cryptoCurrencyAndPrice.split(" ");
    		if (split.length != 3) {
    			queryResult = "格式範例 => 註冊貨幣 eth 40 50";
    		}
    		else {
    			String code = split[0].trim();
    			boolean isCryptoCurrency = CurrencyCnst.isCryptoCurrency(code);
    			if (!isCryptoCurrency) {
    				String supportedCryptoCurrency = CurrencyCnst.getSupportedCryptoCurrency();
    				queryResult = "目前只支援 " + supportedCryptoCurrency + "格式範例 => Ex: 註冊貨幣 eth 40 50";
    			}
    			else {
    				try {
    					CurrencyCnst currency = CurrencyCnst.convert(code);
    					BigDecimal lowerPrice = new BigDecimal(split[1]);
    					BigDecimal upperPrice = new BigDecimal(split[2]);
    					CryptoCurrencyPriceReached priceReached = new CryptoCurrencyPriceReached(currency, lowerPrice, upperPrice);
						registerService.registerCryptoCurrency(userId, priceReached);
						
						DecimalFormat decFormat = new DecimalFormat("###0.00000000");
						
						queryResult = "註冊 " + currency + " 到價通知成功, 價格: " + decFormat.format(lowerPrice.doubleValue()) + " ~ " + decFormat.format(upperPrice.doubleValue());
    				}
    				catch (Exception e) {
    					queryResult = "格式範例 => 註冊貨幣 eth 40 50"; 
    				}
    			}
    		}
    	}
    	// 取消虛擬貨幣匯率到價通知
    	else if (inputMsg.startsWith("取消貨幣")) {
    		String cryptoCurrency = inputMsg.substring(inputMsg.indexOf("取消貨幣") + "取消貨幣".length(), inputMsg.length()).trim();
    		boolean isCryptoCurrency = CurrencyCnst.isCryptoCurrency(cryptoCurrency);
			if (!isCryptoCurrency) {
				queryResult = "目前只支援 " + CurrencyCnst.getSupportedCryptoCurrency() + "格式範例 => 取消貨幣btc, 您輸入: " + cryptoCurrency;
			}
			else {
				CurrencyCnst currency = CurrencyCnst.convert(cryptoCurrency);
				boolean hasRegistered = registerService.hasRegisteredCryptoCurrency(userId, currency);
				if (hasRegistered) {
					registerService.unregisterCryptoCurrency(userId, currency);
					queryResult = "取消註冊 " + currency + " 成功";
				}
				else {
					queryResult = "您未註冊 " + currency + " 到價通知";
				}
			}
    	}
    	// 新增貨幣買賣紀錄
    	else if (inputMsg.contains("買貨幣") || inputMsg.contains("賣貨幣")) {
    		if (inputMsg.contains("買")) {
    			String errorMsg = checkBuySellCryptoCurrencyMsg("買貨幣", inputMsg);
    			if (errorMsg != null) {
    				queryResult = errorMsg;
    			}
    			else {
    				// 新增貨幣買資訊
    				String[] split = inputMsg.split(" ");
    				String buyDateTime = split[0];
					String currencyCode = split[2];
					currencyCode = currencyCode.toUpperCase();
					BigDecimal buyPrice = new BigDecimal(split[3]);
					BigDecimal buyVolumes = new BigDecimal(split[4]);
					BigDecimal feeRate = new BigDecimal(split[5]);
					queryResult = currencyService.addBuyCryptoCurrency(userId, buyDateTime, currencyCode, buyPrice, buyVolumes, feeRate);
    			}
    		}
    		else if (inputMsg.contains("賣")) {
    			String errorMsg = checkBuySellCryptoCurrencyMsg("賣貨幣", inputMsg);
    			if (errorMsg != null) {
    				queryResult = errorMsg;
    			}
    			else {
    				// 新增貨幣賣資訊
    				String[] split = inputMsg.split(" ");
    				String sellDateTime = split[0];
					String currencyCode = split[2];
					currencyCode = currencyCode.toUpperCase();
					BigDecimal sellPrice = new BigDecimal(split[3]);
					BigDecimal sellVolumes = new BigDecimal(split[4]);
					BigDecimal feeRate = new BigDecimal(split[5]);
					queryResult = currencyService.addSellCryptoCurrency(userId, sellDateTime, currencyCode, sellPrice, sellVolumes, feeRate);
    			}
    		}
    	}
    	// 查詢貨幣庫存
    	else if (inputMsg.equals("查詢貨幣庫存")) {
    		queryResult = currencyService.queryTreasuryCryptoCurrency(userId);
    		logger.info(queryResult);
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
    		return new TextMessage(getRandomResponseMsg());
    	}
    }

	private String checkBuySellStockMsg(String buySellKeyWord, String inputMsg) {
		String[] split = inputMsg.split(" ");
		if (split.length != 5) {
			return "格式範例: 2017/3/24 " + buySellKeyWord + " 3088 56.8 2000";
		}
		else {
			// check 日期
			String dateStr = split[0];
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			try {
				dateFormat.parse(dateStr);
			} catch (ParseException e) {
				return "日期格式為 yyyy/MM/dd";
			}
			
			// check 買/賣
			String buySell = split[1];
			if (!buySell.equals(buySellKeyWord)) {
				return "請確認為買股票/賣股票";
			}
			
			// check 股票
			String stockNameOrId = split[2];
			boolean isSupportedStock = stockService.isSupportedStock(stockNameOrId);
			if (!isSupportedStock) {
				return "不支援您輸入的股票";
			}
			
			// check 價格
			String strPrice = split[3];
			try {
				Double.parseDouble(strPrice);
			} catch (Exception e) {
				return "請確認輸入的價格";
			}
			
			// check 股數
			String strShares = split[4];
			try {
				Long.parseLong(strShares);
			} catch (Exception e) {
				return "請確認輸入的股數";
			}
		}
		return null;
	}
	
	private String checkBuySellCryptoCurrencyMsg(String buySellKeyWord, String inputMsg) {
		String[] split = inputMsg.split(" ");
		if (split.length != 6) {
			return "格式範例: 2017/05/08-08:07:30 " + buySellKeyWord + " STR 0.00004900 20000 0.15";
		}
		else {
			// check 日期時間
			String dateStr = split[0];
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
			try {
				dateFormat.parse(dateStr);
			} catch (ParseException e) {
				return "日期時間格式為 yyyy/MM/dd-HH:mm:ss";
			}
			
			// check 買貨幣/賣貨幣
			String buySell = split[1];
			if (!buySell.equals(buySellKeyWord)) {
				return "請確認為買貨幣/賣貨幣";
			}
			
			// check 貨幣
			String currencyCode = split[2];
			boolean isTreasurySupportedCryptoCurrency = CurrencyCnst.isTreasurySupportedCryptoCurrency(currencyCode);
			if (!isTreasurySupportedCryptoCurrency) {
				return "不支援您輸入的虛擬貨幣";
			}
			
			// check 價格
			String strPrice = split[3];
			try {
				Double.parseDouble(strPrice);
			} catch (Exception e) {
				return "請確認輸入的價格";
			}
			
			// check 數量
			String strVolumes = split[4];
			try {
				Double.parseDouble(strVolumes);
			} catch (Exception e) {
				return "請確認輸入的數量";
			}
			
			// check 手續費 
			String strFeeRate = split[5];
			try {
				double feeRate = Double.parseDouble(strFeeRate);
				if (feeRate != 0.15 && feeRate != 0.25) {
					return "請確認手續費為 0.15 或 0.25, 無其他";
				}
			} catch (Exception e) {
				return "請確認輸入的數量";
			}
		}
		return null;
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

	/**
	 * <pre>
	 * 建立查詢功能表
	 * </pre>
	 * 
	 * @param replyToken
	 */
	private void createQueryFuncTemplateMsg(String replyToken) {
		// Create query functions menu
		List<Action> postbackActions = new ArrayList<>();
		
		LineQueryFunction[] lineQueryFuncs = LineQueryFunction.values();
		for (LineQueryFunction lineQueryFunc : lineQueryFuncs) {
			PostbackAction postbackAction = new PostbackAction(lineQueryFunc.getSubItemName(), lineQueryFunc.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(LineQueryFunction.QUERY_MENU_IMG_PATH), LineQueryFunction.QUERY_MENU_TITLE, LineQueryFunction.QUERY_MENU_TEXT, postbackActions);
		
		TemplateMessage message = new TemplateMessage(LineQueryFunction.QUERY_ALT_TEXT, buttonsTemplate);
		reply(replyToken, message);
	}

	/**
	 * <pre>
	 * 建立金融功能表
	 * </pre>
	 * 
	 * @param replyToken
	 */
	private void createFinancialFuncTemplateMsg(String replyToken) {
		// Create financial functions menu
		List<Action> postbackActions = new ArrayList<>();
		
		LineFinancialFunction[] lineFincFuncs = LineFinancialFunction.values();
		for (LineFinancialFunction lineFincFunc : lineFincFuncs) {
			PostbackAction postbackAction = new PostbackAction(lineFincFunc.getSubItemName(), lineFincFunc.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(LineFinancialFunction.FINANCIAL_MENU_IMG_PATH), LineFinancialFunction.FINANCIAL_MENU_TITLE, LineFinancialFunction.FINANCIAL_MENU_TEXT, postbackActions);
		
		TemplateMessage message = new TemplateMessage(LineFinancialFunction.FINANCIAL_ALT_TEXT, buttonsTemplate);
		reply(replyToken, message);
	}

	/**
	 * <pre>
	 * 建立每個功能及其子功能表
	 * 
	 * 參考: <a href="https://github.com/line/line-bot-sdk-java/blob/master/sample-spring-boot-kitchensink/src/main/java/com/example/bot/spring/KitchenSinkController.java">KitchenSinkController</a>
	 * </pre>
	 *
	 * @param lineFunc 
	 * @param replyToken
	 */
	private void createSubFuncTemplateMsg(LineFunction lineFunc, String replyToken) {
		// Create sub functions menu
		List<Action> postbackActions = new ArrayList<>();
		LineSubFunction[] lineSubFuncs = lineFunc.getLineSubFuncs();
		for (LineSubFunction lineSubFunc : lineSubFuncs) {
			PostbackAction postbackAction = new PostbackAction(lineSubFunc.getLabel(), lineFunc.toString() + "&" + lineSubFunc.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(lineFunc.getSubImagePath()), lineFunc.getSubMenuTitle(), lineFunc.getSubMenuText(), postbackActions);
	
		TemplateMessage message = new TemplateMessage(lineFunc.getSubAltText(), buttonsTemplate);
		reply(replyToken, message);
	}

	@EventMapping
    public void handleDefaultMessageEvent(PostbackEvent event) {
        logger.info(">>>>> handle default message event, event: {}", event);
        
        String replyMsg = null;

        UserSource source = (UserSource) event.getSource();
        PostbackContent postbackContent = event.getPostbackContent();
        
        String userId = source.getUserId();
        String replyToken = event.getReplyToken();
        String postbackData = postbackContent.getData();

        // 處理選擇主功能
        if (!postbackData.contains("&")) {
        	String strLineFunc = postbackData;
        	
        	LineFunction lineFunc = null;
        	
        	LineQueryFunction lineQryFunc = LineQueryFunction.convertByName(strLineFunc);
        	LineFinancialFunction lineFincFunc = LineFinancialFunction.convertByName(strLineFunc);

        	if (lineQryFunc != null) {
        		lineFunc = lineQryFunc;
        	}
        	else if (lineFincFunc != null) {
        		lineFunc = lineFincFunc;
        	}

        	// 建立子功能表
        	createSubFuncTemplateMsg(lineFunc, replyToken);

    		return;
        }
        // 處理選擇子功能
        else if (postbackData.contains("&")) {
	        String[] split = postbackData.split("&");
			String strLineFunc = split[0];
	        String strLineSubFunc = split[1];
	        
	        LineQueryFunction lineQryFunc = LineQueryFunction.convertByName(strLineFunc);
	        LineFinancialFunction lineFincFunc = LineFinancialFunction.convertByName(strLineFunc);

	        if (lineQryFunc != null) {
		        switch (lineQryFunc) {
			        case PARKING_LOT_INFO:
			        	ParkingLotSubFunction parkingLotSubFunc = ParkingLotSubFunction.convertByName(strLineSubFunc);
			        	if (parkingLotSubFunc != null) {
			        		replyMsg = handleParkingLotSubFunction(parkingLotSubFunc, userId);
			        	}
			        	break;
			        
			        case WEATHER:
			        	WeatherSubFunction weatherSubFunc = WeatherSubFunction.convertByName(strLineSubFunc);
			        	if (weatherSubFunc != null) {
			        		replyMsg = handleWeatherSubFunction(weatherSubFunc, userId);
			        	}
			        	break;
			        	
			        case RECEIPT_REWARD:
						ReceiptRewardSubFunction receiptRewardSubFunc = ReceiptRewardSubFunction.convertByName(strLineSubFunc);
						if (receiptRewardSubFunc != null) {
							replyMsg = handleReceiptRewardSubFunction(receiptRewardSubFunc, userId);
						}
						break;
						
					case MOVIE:
						MovieSubFunction movieSubFunc = MovieSubFunction.convertByName(strLineSubFunc);
						if (movieSubFunc != null) {
							replyMsg = handleMovieSubFunction(movieSubFunc, userId, replyToken, postbackData);
						}
						break;
		        }
	        }
	        else if (lineFincFunc != null) {
	        	switch (lineFincFunc) {
		        	case STOCK:
		        		StockSubFunction stockSubFunc = StockSubFunction.convertByName(strLineSubFunc);
		        		if (stockSubFunc != null) {
		        			replyMsg = handleStockSubFunction(stockSubFunc, userId, replyToken);
		        		}
		        		break;

					case CRYPTO_CURRENCY:
						CryptoCurrencySubFunction cryptoCurrencySubFunc = CryptoCurrencySubFunction.convertByName(strLineSubFunc);
						if (cryptoCurrencySubFunc != null) {
							String currencyCode = split.length == 3 ? split[2] : null;
							replyMsg = handleCryptoCurrencySubFunction(cryptoCurrencySubFunc, userId, replyToken, currencyCode);
						}
						break;
	        	}
	        }
        }
        
        if (replyMsg != null) {
        	// 回應給 user
        	reply(replyToken, new TextMessage(replyMsg));
        }
    }
	
	/**
	 * 處理停車場子功能
	 * 
	 * @param parkingLotSubFunc
	 * @param userId
	 * @return
	 */
	private String handleParkingLotSubFunction(ParkingLotSubFunction linSubFunc, String userId) {
		LineQueryFunction lineFunc = LineQueryFunction.PARKING_LOT_INFO;
		
		logger.info("----> Prepare to process parking lot, SubFunction: <{}>, UserId: <{}>", linSubFunc, userId);
		
		String replyMsg = ERROR_MSG;
		switch (linSubFunc) {
			case FIND_PARKING_LOT_BY_FUZZY_SEARCH:
				replyMsg = "請輸入關鍵字";
				break;
	
			case FIND_PARING_LOT_BY_NAME:
				replyMsg = "請輸入停車場名稱";
				break;
		}
		
		recordUserCurrentAction(userId, lineFunc, null, linSubFunc, null);
		
		return replyMsg;
	}

	/**
	 * 處理天氣子功能
	 * 
	 * @param weatherSubFunc
	 * @param userId
	 * @return
	 */
	private String handleWeatherSubFunction(WeatherSubFunction lineSubFunc, String userId) {
		LineQueryFunction lineFunc = LineQueryFunction.WEATHER;
		
		logger.info("----> Prepare to process weather, SubFunction: <{}>, UserId: <{}>", lineSubFunc, userId);
		
		String replyMsg = ERROR_MSG;
		switch (lineSubFunc) {
			case LITTLE_HELPER:
			case ONE_WEEK_PREDICTION:
				replyMsg = "請輸入縣市名稱";
				break;
		}
		
		recordUserCurrentAction(userId, lineFunc, null, lineSubFunc, null);
		
		return replyMsg;
	}

	/**
	 * 處理發票子功能
	 * 
	 * @param lineSubFunc
	 * @param userId
	 * @return
	 */
	private String handleReceiptRewardSubFunction(ReceiptRewardSubFunction lineSubFunc, String userId) {
		logger.info("----> Prepare to process receipt reward, SubFunction: <{}>, UserId: <{}>", lineSubFunc, userId);
		
		String replyMsg = ERROR_MSG;
		switch (lineSubFunc) {
	    	case UPDATE_NUMBERS:
	    		receiptRewardService.getNewestReceiptRewards();
	    		replyMsg = "更新成功";
	    		break;
	
			case GET_LAST_TWO_NUMBERS:
				replyMsg = receiptRewardService.getRecentlyRewards();
				break;
		}
		
		return replyMsg;
	}

	/**
	 * 處理電影子功能
	 * 
	 * @param lineSubFunc
	 * @param userId
	 * @param replyToken 
	 * @param postbackData 
	 * @return
	 */
	private String handleMovieSubFunction(MovieSubFunction lineSubFunc, String userId, String replyToken, String postbackData) {
		logger.info("----> Prepare to process movie, SubFunction: <{}>, UserId: <{}>, PostbackData: <{}>", lineSubFunc, userId, postbackData);
		
		String replyMsg = ERROR_MSG;
		
		switch (lineSubFunc) {
			case UPDATE_MOVIE_TIME:
				viewshowMovieService.refreshMovieTimes();
	    		showTimeMovieService.refreshMovieTimes();
	    		miramarMovieService.refreshMovieTimes();
	    		wovieMovieService.refreshMovieTimes();
	    		ambassadorMovieService.refreshMovieTimes();
	    		replyMsg = "更新成功";
				break;

			case QUERY_MOVIE_TIME:
				String[] splits = postbackData.split("&");
				
				// 查詢電影時刻表 meun 選定
				if (splits.length == 2) {
					logger.info("----- Create supported theater companies menu -----");
					createSupportedTheaterCompanyTemplateMsg(replyToken);
				}
				// 影城 menu 選定
				else if (splits.length == 3) {
					String theaterCompanyEnumName = splits[2];
					
					SupprotedTheaterCompany theaterCompany 
						= SupprotedTheaterCompany.convertByEnumName(theaterCompanyEnumName);
					
					MovieTheater[] movieTheaters = theaterCompany.getMovieTheaters();
					
					logger.info("----- Create movie theaters menu, SupprotedTheaterCompany: <{}>, MovieTheater: <{}>", theaterCompany, Arrays.toString(movieTheaters));
					
					createMovieTheatersTemplateMsg(theaterCompany, movieTheaters, replyToken);
				}
				// 戲院 menu 選定
				else if (splits.length == 4) {
					replyMsg = "請輸入上映: 查詢目前上映電影\n輸入電影名稱: 查詢電影時刻表";
					
					String theaterCompanyEnumName = splits[2];
					SupprotedTheaterCompany theaterCompany 
						= SupprotedTheaterCompany.convertByEnumName(theaterCompanyEnumName);
					
					String theaterEnumName = splits[3];
					MovieTheater theater = getTheaterByEnumName(theaterEnumName);
					
					logger.info("---> Process TheaterCompany: <{}>, theater: <{}>, ask user to input", theaterCompany, theater);
					
					QueryMovieInfo queryMovieInfo 
						= new QueryMovieInfo(theaterCompany, theater);
					
					recordUserCurrentAction(userId, LineQueryFunction.MOVIE, null, MovieSubFunction.QUERY_MOVIE_TIME, queryMovieInfo);
				}
				break;
		}
		
		return replyMsg;
	}

	private MovieTheater getTheaterByEnumName(String theaterName) {
		MovieTheater theater = null;

		AmbassadorTheater ambassadorTheater = AmbassadorTheater.convertByEnumName(theaterName);
		if (ambassadorTheater != null) {
			theater = ambassadorTheater;
		}
		MiramarTheater miramarTheater = MiramarTheater.convertByEnumName(theaterName);
		if (miramarTheater != null) {
			theater = miramarTheater;
		}
		ShowTimeTheater showTimeTheater = ShowTimeTheater.convertByEnumName(theaterName);
		if (showTimeTheater != null) {
			theater = showTimeTheater;
		}
		ViewshowTheater viewshowTheater = ViewshowTheater.convertByEnumName(theaterName);
		if (viewshowTheater != null) {
			theater = viewshowTheater;
		}
		return theater;
	}

	/**
	 * 開啟支援的影城功能表
	 * 
	 * @param replyToken
	 */
	private void createSupportedTheaterCompanyTemplateMsg(String replyToken) {
		final String menuTitle = "影城查詢";
		final String menuText = "提供下列影城";
		final String altText = "影城查詢";
		
		// Create sub functions menu
		List<Action> postbackActions = new ArrayList<>();
		
		SupprotedTheaterCompany[] supprotedTheaterCompanies = SupprotedTheaterCompany.values();
		for (int i = 0; i < LINE_TEMPLATE_MSG_MAX_ITEMS; i++) {
			SupprotedTheaterCompany supprotedTheaterCompany = supprotedTheaterCompanies[i];
			PostbackAction postbackAction
				= new PostbackAction(supprotedTheaterCompany.getTheaterCompanyName(), LineQueryFunction.MOVIE + "&" + MovieSubFunction.QUERY_MOVIE_TIME + "&" + supprotedTheaterCompany.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(LineQueryFunction.QUERY_MENU_IMG_PATH), menuTitle, menuText, postbackActions);
	
		TemplateMessage message = new TemplateMessage(altText, buttonsTemplate);
		reply(replyToken, message);
	}
	
	/**
	 * 開起支援影城提供戲院功能表
	 * 
	 * @param theaterCompany 
	 * @param movieTheaters
	 * @param replyToken
	 */
	private void createMovieTheatersTemplateMsg(SupprotedTheaterCompany theaterCompany, MovieTheater[] movieTheaters, String replyToken) {
		final String menuTitle = theaterCompany.getTheaterCompanyName() + "查詢";
		final String menuText = "提供下列戲院";
		final String altText = "戲院查詢";
		
		// Create sub functions menu
		List<Action> postbackActions = new ArrayList<>();
		
		for (int i = 0; i < movieTheaters.length && i < LINE_TEMPLATE_MSG_MAX_ITEMS; i++) {
			MovieTheater movieTheater = movieTheaters[i];
			PostbackAction postbackAction
				= new PostbackAction(movieTheater.getChineseName(), LineQueryFunction.MOVIE + "&" + MovieSubFunction.QUERY_MOVIE_TIME + "&" + theaterCompany.toString() + "&" + movieTheater.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(LineQueryFunction.QUERY_MENU_IMG_PATH), menuTitle, menuText, postbackActions);
	
		TemplateMessage message = new TemplateMessage(altText, buttonsTemplate);
		reply(replyToken, message);
	}

	/**
	 * 處理虛擬貨幣子功能
	 * 
	 * @param lineSubFunc
	 * @param userId
	 * @param replyToken
	 * @param currencyCode
	 * 
	 * @return
	 */
    private String handleCryptoCurrencySubFunction(CryptoCurrencySubFunction lineSubFunc, String userId, String replyToken, String currencyCode) {
    	logger.info("----> Prepare to process crypto currency, SubFunction: <{}>, UserId: <{}>, Data: <{}>", lineSubFunc, userId, currencyCode);
    	
    	String replyMsg = "";
    	switch (lineSubFunc) {
	    	case QUERY_CRYPTO_CURRENCY_PRICE:
	    		// 選擇查詢貨幣匯率, 顯示可以的貨幣供選擇
	    		if (currencyCode == null) {
	    			// FIXME 一次只能 reply 四個, 所以也不用 reply 多次了
		    		int nextIndexToProcess = 0;
		    		CurrencyCnst[] cryptoCurrencys = CurrencyCnst.getCryptoCurrency();
		    		while (nextIndexToProcess < cryptoCurrencys.length) {
		    			nextIndexToProcess = createCrypteCurrencyPriceTemplateMsg(nextIndexToProcess, cryptoCurrencys, replyToken);
		    		}
	    		}
	    		// 確定選擇了貨幣
	    		else {
	    			CurrencyCnst currency = CurrencyCnst.convert(currencyCode);
	    			if (CurrencyCnst.isCryptoCurrency(currencyCode)) {
	    				CurrencyPair currencyPair = null;
	        			switch (currency) {
	    					case BTC:
	    						currencyPair = CurrencyPair.BTC_USD;
	    						break;
	    						
	    					case ETH:
	    						currencyPair = CurrencyPair.ETH_USD;
	    			    		break;
	    			    		
	    					case LTC:
	    						currencyPair = CurrencyPair.LTC_USD;
	    						break;
	    						
	    					case STR:
	    						currencyPair = CurrencyPair.STR_BTC;
	    						break;
	    						
	    					case XRP:
	    						currencyPair = CurrencyPair.XRP_BTC;
	    						break;

	    					default:
	    						// 正常程式不會到這
	    						logger.error("handleCryptoCurrencySubFunction QUERY_CRYPTO_CURRENCY_PRICE got unexpected CurrencyCnst: <{}>", currency);
	    						break;
	        			}
	        			if (currencyPair != null) {
	        				replyMsg = currencyService.getCryptoCurrencyPriceFromExchanges(currencyPair);
	        			}
	         		}
	    		}
				break;
    	
			case HIT_PRICE_INFO:
				boolean hasRegistered = registerService.hasRegisteredCryptoCurrency(userId);
	    		if (hasRegistered) {
	    			replyMsg = registerService.getCryptoCurrencyPricesReachedInfos(userId);
	    		}
	    		else {
	    			replyMsg = "您未註冊任何貨幣到價通知";
	    		}
				break;

			case QUERY_CRYPTO_CURRENCT_TREASURY:
				replyMsg = currencyService.queryTreasuryCryptoCurrency(userId);
				break;
    	}
		return replyMsg;
	}

    /**
     * 建立查詢虛擬貨幣匯率功能表
     * 
     * @param nextIndexToProcess
     * @param cryptoCurrencys
     * @param replyToken
     * @return
     */
    private int createCrypteCurrencyPriceTemplateMsg(int nextIndexToProcess, CurrencyCnst[] cryptoCurrencys, String replyToken) {
    	final String menuTitle = "虛擬貨幣匯率查詢";
    	final String menuText = "提供下列虛擬貨幣";
    	final String altText = "虛擬貨幣匯率查詢";
    	
    	// 開始要處理的 index
    	int indexToProcess = nextIndexToProcess != 0 ? nextIndexToProcess : 0;
    	
    	// 紀錄處理了幾筆
    	int processedCounts = 0;
    	
    	// Create sub functions menu
		List<Action> postbackActions = new ArrayList<>();

		// 一次只能傳四個 menu
		for (int i = indexToProcess; i < cryptoCurrencys.length && processedCounts < LINE_TEMPLATE_MSG_MAX_ITEMS; i++, processedCounts++, indexToProcess++) {
			CurrencyCnst cryptoCurrency = cryptoCurrencys[i];
			PostbackAction postbackAction 
				= new PostbackAction(cryptoCurrency.toString(), LineFinancialFunction.CRYPTO_CURRENCY + "&" + CryptoCurrencySubFunction.QUERY_CRYPTO_CURRENCY_PRICE + "&" + cryptoCurrency.toString());
			postbackActions.add(postbackAction);
		}
		
		ButtonsTemplate buttonsTemplate 
			= new ButtonsTemplate(createUri(LineQueryFunction.QUERY_MENU_IMG_PATH), menuTitle, menuText, postbackActions);
	
		TemplateMessage message = new TemplateMessage(altText, buttonsTemplate);
		reply(replyToken, message);
		
		return indexToProcess;
    }

    /**
	 * 處理股票子功能
	 * 
	 * @param lineSubFunc
	 * @param userId
	 * @param replyToken
	 * 
	 * @return
	 */
	private String handleStockSubFunction(StockSubFunction lineSubFunc, String userId, String replyToken) {
		LineFinancialFunction lineFunc = LineFinancialFunction.STOCK;
		
		logger.info("----> Prepare to process stock, SubFunction: <{}>, UserId: <{}>, Data: <{}>", lineSubFunc, userId, null);
		
		String replyMsg = ERROR_MSG;
		switch (lineSubFunc) {
			case UPDATE_STOCK_INFOS:
				stockService.refreshStockInfo();
				replyMsg = "更新成功";
				break;

			case QUERY_MATCH_PRICE:
				replyMsg = "請輸入股票名稱或代號";
				recordUserCurrentAction(userId, null, lineFunc, lineSubFunc, null);
				break;

			case HIT_PRICE_INFO:
				boolean hasRegistered = registerService.hasRegisteredStock(userId);
	    		if (hasRegistered) {
	    			replyMsg = registerService.getStockPricesReachedInfos(userId);
	    		}
	    		else {
	    			replyMsg = "您未註冊任何到價通知";
	    		}
				break;

			case QUERY_STOCK_TREASURY:
				replyMsg = stockService.queryTreasuryStock(userId);
				break;
		}
		return replyMsg;
	}

	/**
	 * 紀錄目前使用者在進行的查詢功能 
	 * 
	 * @param userId
	 * @param lineQryFunc
	 * @param lineFincFunc
	 * @param subFunc
	 * @param movieTheater
	 */
	private void recordUserCurrentAction(String userId, LineQueryFunction lineQryFunc, LineFinancialFunction lineFincFunc, LineSubFunction lineSubFunc, QueryMovieInfo queryMovieInfo) {
		CurrentFunction currentFunc = userCurrentFunc.get(userId);
		if (currentFunc == null) {
			currentFunc = new CurrentFunction(lineQryFunc, lineFincFunc, lineSubFunc, queryMovieInfo);
		}
		else {
			currentFunc.setLineQueryFunc(lineQryFunc);
			currentFunc.setLineFincFunc(lineFincFunc);
			currentFunc.setLineSubFunc(lineSubFunc);
			currentFunc.setQueryMovieInfo(queryMovieInfo);
		}
		userCurrentFunc.put(userId, currentFunc);
	}
	
	/**
	 * 取得目前使用者在進行的功能
	 */
	private CurrentFunction getUserCurrentFunction(String userId) {
		return userCurrentFunc.get(userId);
	}
	
	/**
	 * 移除目前使用者在進行的功能
	 */
	private void removeUserCurrentFunction(String userId) {
		CurrentFunction remove = userCurrentFunc.remove(userId);
		logger.info("~~~~ UserId: <{}> finish current function: <{}>", userId, remove);
	}
 
	/**
	 * 處理目前使用者在進行的查詢子功能
	 * 
	 * @param userId 
	 * @param replyToken 
	 * @param currentFunction
	 * @param inputMsg 
	 */
    private void processUserCurrentQryFunction(String userId, String replyToken, CurrentFunction currentFunction, String inputMsg) {
    	LineQueryFunction lineQryFunc = currentFunction.getLineQueryFunc();
    	LineSubFunction lineSubFunc = currentFunction.getLineSubFunc();
    	
		logger.info("----> Prepare to process userId: <{}>, LineQueryFunction: <{}>, LineSubFunction: <{}>, input: <{}>", userId, lineQryFunc, lineSubFunc, inputMsg);
		
		String replyMsg = null;
		
		switch (lineQryFunc) {
			case PARKING_LOT_INFO:
				ParkingLotSubFunction parkingLotSubFunc = (ParkingLotSubFunction) lineSubFunc;
				switch (parkingLotSubFunc) {
					case FIND_PARKING_LOT_BY_FUZZY_SEARCH:
						replyMsg = parkingLotService.findByNameLike(inputMsg);
						break;
						
					case FIND_PARING_LOT_BY_NAME:
						replyMsg = parkingLotService.findByName(inputMsg);
						break;
				}
				break;
				
			case WEATHER:
				WeatherSubFunction weatherSubFunction = (WeatherSubFunction) lineSubFunc;
				switch (weatherSubFunction) {
					case LITTLE_HELPER:
						replyMsg = cwbService.getWeatherLittleHelperByCity(inputMsg);
						break;

					case ONE_WEEK_PREDICTION:
						replyMsg = cwbService.getOneWeekWeatherPrediction(inputMsg);
						break;
				}
				break;
				
			case MOVIE:
				MovieSubFunction movieSubFunction = (MovieSubFunction) lineSubFunc;
				switch (movieSubFunction) {
					case QUERY_MOVIE_TIME:
						QueryMovieInfo qryMovieInfo = currentFunction.getQueryMovieInfo();
						
						String NOW_PLAYING_KEYWORD = "上映";

						// 根據不同戲院來查詢
						SupprotedTheaterCompany theaterCompany = qryMovieInfo.getTheaterCompany();
						MovieTheater movieTheater = qryMovieInfo.getMovieTheater();
						
						String theaterName = movieTheater.getChineseName();
						
						switch (theaterCompany) {
							case AMBASSADOR:
								if (inputMsg.equals(NOW_PLAYING_KEYWORD)) {
									replyMsg = ambassadorMovieService.queryNowPlayingByTheaterName(theaterName);
								}
								else {
									replyMsg = ambassadorMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theaterName, inputMsg);
								}
								break;

							case MIRAMAR:
								if (inputMsg.equals(NOW_PLAYING_KEYWORD)) {
									replyMsg = miramarMovieService.queryNowPlayingByTheaterName(theaterName);
								}
								else {
									replyMsg = miramarMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theaterName, inputMsg);
								}
								break;

							case SHOWTIME:
								if (inputMsg.equals(NOW_PLAYING_KEYWORD)) {
									replyMsg = showTimeMovieService.queryNowPlayingByTheaterName(theaterName);
								}
								else {
									replyMsg = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theaterName, inputMsg);
								}
								break;

							case VIEWSHOW:
								if (inputMsg.equals(NOW_PLAYING_KEYWORD)) {
									replyMsg = viewshowMovieService.queryNowPlayingByTheaterName(theaterName);
								}
								else {
									replyMsg = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike(theaterName, inputMsg);
								}
								break;
						}
						break;
	
					default:
						logger.warn("---> Upexpected MovieSubFunction received: <{}>, please checked...", movieSubFunction);
						break;
				}

			default:
				break;
		}
		
		removeUserCurrentFunction(userId);
		
		if (replyMsg != null) {
			reply(replyToken, new TextMessage(replyMsg));
		}
	}
    
	/**
	 * 處理目前使用者在進行的金融子功能
	 * 
	 * @param userId 
	 * @param replyToken 
	 * @param currentFunction
	 * @param inputMsg 
	 */
    private void processUserCurrentFincFunction(String userId, String replyToken, CurrentFunction currentFunction, String inputMsg) {
    	LineFinancialFunction lineFincFunc = currentFunction.getLineFincFunc();
    	LineSubFunction lineSubFunc = currentFunction.getLineSubFunc();

    	logger.info("----> Prepare to process userId: <{}>, LineFinancialFunction: <{}>, LineSubFunction: <{}>, input: <{}>", userId, lineFincFunc, lineSubFunc, inputMsg);
		
		String replyMsg = ERROR_MSG;
		
		switch (lineFincFunc) {
			case STOCK:
				StockSubFunction stockSubFunc = (StockSubFunction) lineSubFunc;
				switch (stockSubFunc) {
					case QUERY_MATCH_PRICE:
						replyMsg = stockService.getStockPriceStrByNameOrId(inputMsg);
						break;

					default:
						// 正常程式不會到這
						logger.error("processUserCurrentFincFunction got unexpected StockSubFunction: <{}>", stockSubFunc);
						break;
				}
				break;

			case CRYPTO_CURRENCY:
				// TODO other sub func
				break;
		}
		
		removeUserCurrentFunction(userId);
		
		reply(replyToken, new TextMessage(replyMsg));
	}

	/**
     * 回應單則訊息
     * 
     * @param replyToken
     * @param message
     */
	private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

	/**
	 * 回應多則訊息
	 * 
	 * @param replyToken
	 * @param messages
	 */
    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
        	BotApiResponse apiResponse = null;
        	if (messages.size() > LINE_MAXIMUM_REPLY_MSG_SIZE) {
        		List<Message> errorMsg = Arrays.asList(new TextMessage[] {new TextMessage("資料數目超過可回傳訊息")});
        		
        		apiResponse 
            		= lineMessagingClient
                    	.replyMessage(new ReplyMessage(replyToken, errorMsg))
                    	.get();

        		logger.warn("reply done ---> messages size: <{}> over LINE_MAXIMUM_REPLY_MSG_SIZE: <{}>", messages.size(), LINE_MAXIMUM_REPLY_MSG_SIZE);
        	}
        	else {
        		logger.info("prepare to reply ---> content: {}", messages);
        		
	            apiResponse 
	            	= lineMessagingClient
	                    .replyMessage(new ReplyMessage(replyToken, messages))
	                    .get();
	            
	            logger.info("reply done ---> reply message done");
        	}
            logger.info("Sent messages and got response: {}", apiResponse);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Exception raised while tring to reply", e);
        }
    }
    
    /**
     * 建立圖檔 uri
     * 
     * @param path
     * @return
     */
    private static String createUri(String path) {
        return ServletUriComponentsBuilder
        			.fromCurrentContextPath()
        			.path(path).build()
        			.toUriString();
    }
}
