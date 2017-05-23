package com.weatherrisk.api.service.currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.BuySell;
import com.weatherrisk.api.cnst.currency.CryptoCurrencyExchange;
import com.weatherrisk.api.cnst.currency.CurrencyCnst;
import com.weatherrisk.api.model.currency.CryptoCurrencyBSRecord;
import com.weatherrisk.api.model.currency.CryptoCurrencyBSRecordRepository;
import com.weatherrisk.api.model.currency.TreasuryCryptoCurrency;
import com.weatherrisk.api.model.currency.TreasuryCryptoCurrencyRepository;

@Service
public class CurrencyService {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyService.class);
	
	private final DecimalFormat cryptoCurrencyDecFormat = new DecimalFormat("0.00000000");
	
	private final DecimalFormat twdFormat = new DecimalFormat("#");
	
	@Autowired
	private CryptoCurrencyBSRecordRepository cryptoCurrencyBSRecordRepo;
	
	@Autowired
	private TreasuryCryptoCurrencyRepository treasuryCryptoCurrencyRepo;
	
	/**
	 * 從各大交易所取得指定需擬貨幣目前價格
	 */
	public String getCryptoCurrencyPriceFromExchanges(CurrencyPair currencyPair) {
		StringBuilder buffer = new StringBuilder();
		try {
			BigDecimal usdTwdRate = getBuyCashRatesFromTaiwanBank(CurrencyCnst.USD);
			
			CryptoCurrencyExchange cryptoCurrencyExchange = null;
			CurrencyCnst currencyCnst = null;
			if (currencyPair.equals(CurrencyPair.BTC_USD)) {
				cryptoCurrencyExchange = CryptoCurrencyExchange.BTCE;
				currencyCnst = CurrencyCnst.BTC;
			}
			else if (currencyPair.equals(CurrencyPair.ETH_USD)) {
				cryptoCurrencyExchange = CryptoCurrencyExchange.BTCE;
				currencyCnst = CurrencyCnst.ETH;
			}
			else if (currencyPair.equals(CurrencyPair.LTC_USD)) {
				cryptoCurrencyExchange = CryptoCurrencyExchange.BTCE;
				currencyCnst = CurrencyCnst.LTC;
			}
			else if (currencyPair.equals(CurrencyPair.STR_BTC)) {
				cryptoCurrencyExchange = CryptoCurrencyExchange.POLONIEX;
				currencyCnst = CurrencyCnst.STR;
			}
			else if (currencyPair.equals(CurrencyPair.XRP_BTC)) {
				cryptoCurrencyExchange = CryptoCurrencyExchange.POLONIEX;
				currencyCnst = CurrencyCnst.XRP;
			}
			getCryptoCurrencyPriceFromExchange(buffer, cryptoCurrencyExchange.getExchangeName(),
					cryptoCurrencyExchange.getExchangeClassName(), currencyCnst, currencyPair, usdTwdRate);
			
			return buffer.toString();
		} catch (Exception e) {
			logger.error("Exception raised while trying to get price", e);
			return "抓取價格失敗";
		}
	}
	
	/**
	 * <pre>
	 * 從指定交易所取得價格
	 * 
	 * 參考: <a href="https://github.com/timmolter/XChange/blob/develop/xchange-examples/src/main/java/org/knowm/xchange/examples/bitstamp/marketdata/BitstampTickerDemo.java">Bitstamp Ticker Demo</a>
	 * </pre>
	 */
	private void getCryptoCurrencyPriceFromExchange(StringBuilder buffer, String exchangeName, String exchangeClassName,
			CurrencyCnst baseCurrency, CurrencyPair currencyPair, BigDecimal usdTwdRate) throws Exception {
		Ticker ticker = getTickerByCurrencyPairFromExchange(exchangeClassName, currencyPair);
		
		DateFormat updateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DecimalFormat outDecFormat = new DecimalFormat("#.##");
		
		// Poloniex 回傳資料比較特別
		if (exchangeClassName != PoloniexExchange.class.getName()) {
			processNonPoloniexExchange(buffer, exchangeName, baseCurrency, usdTwdRate, ticker, updateTimeFormat, outDecFormat);
		}
		else {
			processPoloniexExchange(buffer, exchangeName, baseCurrency, usdTwdRate, ticker, outDecFormat);
		}
	}

	/**
	 * <pre>
	 * 從指定交易所取得對應貨幣的 Ticker
	 * </pre>
	 * 
	 * @param exchangeClassName
	 * @param currencyPair
	 * @return
	 * @throws IOException
	 */
	private Ticker getTickerByCurrencyPairFromExchange(String exchangeClassName, CurrencyPair currencyPair) throws IOException {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

		MarketDataService marketDataService = exchange.getMarketDataService();
		
		Ticker ticker = marketDataService.getTicker(currencyPair);

		return ticker;
	}

	/**
	 * 處理非 Poloniex 交易所回傳字串
	 * 
	 * @param buffer
	 * @param exchangeName
	 * @param baseCurrency
	 * @param usdTwdRate
	 * @param ticker
	 * @param updateTimeFormat
	 * @param outDecFormat
	 */
	private void processNonPoloniexExchange(StringBuilder buffer, String exchangeName, CurrencyCnst baseCurrency, BigDecimal usdTwdRate,
			Ticker ticker, DateFormat updateTimeFormat, DecimalFormat outDecFormat) {
		buffer.append(exchangeName).append(":\n");
		buffer.append("目前成交價 ").append(ticker.getCurrencyPair()).append(": ").append(ticker.getLast()).append("\n");
		buffer.append("換算台幣價 ").append(baseCurrency).append("/TWD: ").append(outDecFormat.format(usdTwdRate.multiply(ticker.getLast()))).append("\n");
		buffer.append("最高價 ").append(baseCurrency).append("/USD: ").append(ticker.getHigh()).append("\n");
		buffer.append("最低價 ").append(baseCurrency).append("/USD: ").append(ticker.getLow()).append("\n");
		if (ticker.getTimestamp() != null) {
			buffer.append("更新時間: ").append(updateTimeFormat.format(ticker.getTimestamp())).append("\n");
		}
		buffer.append("USD/TWD: ").append(usdTwdRate).append("\n");
		buffer.append("(備註: USD/TWD匯率, 參考台灣銀行現金買入)");
	}
	
	/**
	 * 處理 Poloniex 交易所回傳字串
	 * 
	 * @param buffer
	 * @param exchangeName
	 * @param baseCurrency
	 * @param usdTwdRate
	 * @param ticker
	 * @param outDecFormat
	 * @throws Exception
	 */
	private void processPoloniexExchange(StringBuilder buffer, String exchangeName, CurrencyCnst baseCurrency,
			BigDecimal usdTwdRate, Ticker ticker, DecimalFormat outDecFormat) throws Exception {
		BigDecimal btcUsdRate = getCryptoLastPriceFromBtcE(CurrencyPair.BTC_USD);

		buffer.append(exchangeName).append(":\n");
		buffer.append("目前成交價 ").append(ticker.getCurrencyPair()).append(": ").append(ticker.getLast()).append("\n");
		buffer.append("換算台幣價 ").append(baseCurrency).append("/TWD: ").append(outDecFormat.format(usdTwdRate.multiply(btcUsdRate.multiply(ticker.getLast())))).append("\n");
		buffer.append("最高價 ").append(baseCurrency).append("/BTC: ").append(ticker.getHigh()).append("\n");
		buffer.append("最低價 ").append(baseCurrency).append("/BTC: ").append(ticker.getLow()).append("\n");
		buffer.append("BTC/USD: ").append(btcUsdRate).append("\n");
		buffer.append("USD/TWD: ").append(usdTwdRate).append("\n");
		buffer.append("(備註: BTC/USD匯率, 參考 BTC-E)\n");
		buffer.append("(備註: USD/TWD匯率, 參考台灣銀行現金買入)");
	}
	
	/**
	 * 從 BTC-E 取得某一檔虛擬貨幣的價格
	 * 
	 * @param currencyPair
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getCryptoLastPriceFromBtcE(CurrencyPair currencyPair) throws Exception {
		Ticker ticker = getTickerByCurrencyPairFromExchange(BTCEExchange.class.getName(), currencyPair);
		return ticker.getLast();
	}
	
	/**
	 * 從 Poloneix 取得某一檔虛擬貨幣的價格
	 * 
	 * @param currencyPair
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getCryptoLastPriceFromPoloneix(CurrencyPair currencyPair) throws Exception {
		Ticker ticker = getTickerByCurrencyPairFromExchange(PoloniexExchange.class.getName(), currencyPair);
		return ticker.getLast();
	}

	/**
     * <pre>
     * 從台灣銀行取得外幣對台幣匯率
     * </pre>
	 */
	public String getRealCurrencyRatesFromTaiwanBank(CurrencyCnst currency) {
		try {
			Map<String, ?> currencyRatesMap = getCurrencyRatesMapFromTaiwanBank(currency);
			
			String buyCashRate = (String) currencyRatesMap.get("buyCash");
			String sellCashRate = (String) currencyRatesMap.get("sellCash");
			
			StringBuilder buffer = new StringBuilder();
			buffer.append(currency.toString()).append("/TWD (台灣銀行現金買入): ").append(buyCashRate).append("\n");
			buffer.append(currency.toString()).append("/TWD (台灣銀行現金賣出): ").append(sellCashRate);
			
			return buffer.toString();
			
		} catch (Exception e) {
			logger.error("Exception raised while trying to get price", e);
			return "抓取價格失敗";
		}
	}

	/**
	 * <pre>
	 * 從台灣銀行取得外幣對台幣銀行現金買進匯率
	 * 
	 * 參考: <a href="http://blog.asper.tw/2015/05/json.html">取得匯率方法</a>
	 * </pre> 
	 */
	private BigDecimal getBuyCashRatesFromTaiwanBank(CurrencyCnst currency) throws Exception {
		Map<String, ?> currencyRatesMap = getCurrencyRatesMapFromTaiwanBank(currency);
		
		String strBuyCash = (String) currencyRatesMap.get("buyCash");
		
		return new BigDecimal(strBuyCash); 
	}

	@SuppressWarnings("unchecked")
	private Map<String, ?> getCurrencyRatesMapFromTaiwanBank(CurrencyCnst currency) throws Exception {
		StringBuilder srcBuffer = new StringBuilder();
		BufferedReader reader = null;
	
		try {
			URL url = new URL("http://asper-bot-rates.appspot.com/currency.json");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				srcBuffer.append(line);
			}
		} finally {
			IOUtils.closeQuietly(reader);
		}

		String jsonResp = srcBuffer.toString();
		
		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, ?> map = objectMapper.readValue(jsonResp, HashMap.class);
		
		Map<String, ?> ratesMap = (Map<String, ?>) map.get("rates");
		
		Map<String, ?> currencyRatesMap = (Map<String, ?>) ratesMap.get(currency.toString());

		return currencyRatesMap;
	}
	
	/**
	 * <pre>
	 * 從 winkdex 取得目前 BTC 價格
	 * 
	 * 參考: <a href="https://breekmd.wordpress.com/2015/03/11/bitcoin-price-with-winkdex-api-in-java-part-i/">Bitcoin Price with Winkdex api</a>
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String getBitcoinPriceFromWinkdex() {
		StringBuffer srcBuffer = new StringBuffer();

		BufferedReader reader = null;
		try {
			URL url = new URL("https://winkdex.com/api/v0/price");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "breekmd.wordpress.com-tutorial");
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				srcBuffer.append(line);
			}
		} catch (IOException e) {
			logger.error("IOException raised while trying to get Bitcoin price from WINKDEXSM", e);
			return "從 WINKDEXSM 抓取資料失敗";
		} finally {
			IOUtils.closeQuietly(reader);
		}

		Map<String, ?> map = new HashMap<>();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			map = objectMapper.readValue(srcBuffer.toString(), HashMap.class);
		} catch (IOException e) {
			logger.error("IOException raised while read json string to hashmap", e);
			return "從 WINKDEXSM 抓取價格失敗";
		}
		
		DateFormat srcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		String timestamp = null;
		Date date = null;
		try {
			timestamp = (String) map.get("timestamp");
			date = srcDateFormat.parse(timestamp);
		} catch (ParseException e) {
			logger.error("ParseException raised while parsing timestamp to Date, timestamp: <{}>", timestamp, e);
			return "從 WINKDEXSM 抓取價格失敗";
		}
		
		Integer iPrice = (Integer) map.get("price");
		Double price = Double.parseDouble(String.valueOf(iPrice.intValue())) / 100;
		
		DateFormat outDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		DecimalFormat outDecFormat = new DecimalFormat("#.##");
		
		StringBuilder outBuffer = new StringBuilder();
		
		outBuffer.append("日期: ").append(outDateFormat.format(date)).append("\n");
		outBuffer.append("價格: $").append(outDecFormat.format(price)).append("\n");
		outBuffer.append("由 WINKDEXSM 提供");
		
		return outBuffer.toString();
	}

	/**
	 * 買進虛擬貨幣
	 * 
	 * @param userId
	 * @param buyDateTime
	 * @param currencyCode
	 * @param buyPrice
	 * @param buyVolumes
	 * @return
	 */
	public String addBuyCryptoCurrency(String userId, String buyDateTime, String currencyCode, BigDecimal buyPrice, BigDecimal buyVolumes) {
		CryptoCurrencyBSRecord bsRecord = null;
		
		// 新增買進紀錄
		try {
			bsRecord = addBuySellRecord(userId, currencyCode, BuySell.BUY, buyDateTime, buyPrice, buyVolumes);
		} catch (ParseException e) {
			return "新增失敗, 因日期時間格式錯誤, 格式:yyyy/MM/dd-HH:mm";
		}
		
		// 更新庫存資訊
		updateTreasuryCurrency(bsRecord);
		
		// 更新 BTC 庫存資訊 (如果要回復測試資料, 要把這個 mark 掉, 不然會亂掉, 之後穩定就留著)
		updateTreasuryBtc(bsRecord);

		// 回傳訊息
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(buyDateTime);
		buffer.append(" 買進 (").append(currencyCode).append(")");
		buffer.append(" $").append(decFormat.format(buyPrice.doubleValue())).append(" ");
		buffer.append(decFormat.format(buyVolumes)).append("顆").append(", 資訊儲存成功");

		return buffer.toString();
	}

	/**
	 * 賣出虛擬貨幣
	 * 
	 * @param userId
	 * @param sellDateTime
	 * @param currencyCode
	 * @param sellPrice
	 * @param sellVolumes
	 * @return
	 */
	public String addSellCryptoCurrency(String userId, String sellDateTime, String currencyCode, BigDecimal sellPrice, BigDecimal sellVolumes) {
		CryptoCurrencyBSRecord bsRecord = null;
		
		// 新增賣出紀錄
		try {
			bsRecord = addBuySellRecord(userId, currencyCode, BuySell.SELL, sellDateTime, sellPrice, sellVolumes);
		} catch (ParseException e) {
			return "新增失敗, 因日期時間格式錯誤, 格式:yyyy/MM/dd-HH:mm";
		}

		// 更新非 BTC 庫存資訊
		updateTreasuryCurrency(bsRecord);
		
		// 更新 BTC 庫存資訊
		updateTreasuryBtc(bsRecord);

		// 回傳訊息
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(sellDateTime);
		buffer.append(" 賣出 (").append(currencyCode).append(")");
		buffer.append(" $").append(decFormat.format(sellPrice.doubleValue())).append(" ");
		buffer.append(decFormat.format(sellVolumes)).append("顆").append(", 資訊儲存成功");

		return buffer.toString();
	}

	/**
	 * 新增虛擬貨幣買賣紀錄
	 * 
	 * @param userId
	 * @param currencyCode
	 * @param buySell
	 * @param dateTime
	 * @param price
	 * @param volumes
	 * @return
	 * @throws ParseException
	 */
	private CryptoCurrencyBSRecord addBuySellRecord(String userId, String currencyCode, BuySell buySell, String dateTime, BigDecimal price, BigDecimal volumes) throws ParseException {
		CryptoCurrencyBSRecord currencyRecord = new CryptoCurrencyBSRecord();
		try {
			currencyRecord.setData(userId, currencyCode, buySell, dateTime, price, volumes);
		} catch (ParseException e) {
			logger.error("Exception raised while paring dateTime, the correct format is 'yyyy/MM/dd-HH:mm:ss'");
			throw e;
		}
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to insert currency record, {}...", currencyRecord);
		cryptoCurrencyBSRecordRepo.insert(currencyRecord);
		logger.info("<<<<< Insert currency record done, time-spent: <{} ms>", (System.currentTimeMillis() - startTime));
		
		return currencyRecord;
	}

	/**
	 * 更新非 BTC 庫存資訊
	 * 
	 * @param bsRecord
	 */
	private void updateTreasuryCurrency(CryptoCurrencyBSRecord bsRecord) {
		String userId = bsRecord.getUserId();
		String currencyCode = bsRecord.getCurrencyCode();
		BuySell buySell = bsRecord.getBuySell();
		
		String id = TreasuryCryptoCurrency.getId(userId, currencyCode);
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to get treasury currency by id: <{}>...", id);
		TreasuryCryptoCurrency existData = treasuryCryptoCurrencyRepo.findOne(id);
		logger.info("<<<<< Get treasury currency by id: <{}> done, time-spent: <{} ms>", id, (System.currentTimeMillis() - startTime));
		
		// 代表全新, 要新增
		if (existData == null) {
			TreasuryCryptoCurrency newData = new TreasuryCryptoCurrency();
			newData.setNewDataNonBtc(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to insert treasury currency: <{}>...", newData);
			treasuryCryptoCurrencyRepo.insert(newData);
			logger.info("<<<<< Insert treasury currency: <{}> done, time-spent: <{} ms>", newData, (System.currentTimeMillis() - startTime));
		}
		// 買進資料更新
		else if (BuySell.BUY == buySell) {
			existData.buyUpdateExistDataNonBtc(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to update treasury currency: <{}>...", existData);
			treasuryCryptoCurrencyRepo.save(existData);
			logger.info("<<<<< Update treasury currency: <{}> done, time-spent: <{} ms>", existData, (System.currentTimeMillis() - startTime));
		}
		// 賣出資料更新
		else if (BuySell.SELL == buySell) {
			existData.sellUpdateExistDataNonBtc(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to update treasury currency: <{}>...", existData);
			treasuryCryptoCurrencyRepo.save(existData);
			logger.info("<<<<< Update treasury currency: <{}> done, time-spent: <{} ms>", existData, (System.currentTimeMillis() - startTime));
		}
	}
	
	/**
	 * 更新 BTC 庫存資訊
	 * 
	 * @param bsRecord
	 */
	private void updateTreasuryBtc(CryptoCurrencyBSRecord bsRecord) {
		String userId = bsRecord.getUserId();
		String currencyCode = CurrencyCnst.BTC.toString();
		BuySell buySell = bsRecord.getBuySell();
		
		String id = TreasuryCryptoCurrency.getId(userId, currencyCode);
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to get treasury currency by id: <{}>...", id);
		TreasuryCryptoCurrency existData = treasuryCryptoCurrencyRepo.findOne(id);
		logger.info("<<<<< Get treasury currency by id: <{}> done, time-spent: <{} ms>", id, (System.currentTimeMillis() - startTime));
		
		// 代表全新, 要新增
		if (existData == null) {
			// FIXME 目前先建了, 所以不會有這個
		}
		// 買進資料更新
		else if (BuySell.BUY == buySell) {
			existData.buyUpdateExistDataBtc(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to update treasury currency: <{}>...", existData);
			treasuryCryptoCurrencyRepo.save(existData);
			logger.info("<<<<< Update treasury currency: <{}> done, time-spent: <{} ms>", existData, (System.currentTimeMillis() - startTime));
		}
		// 賣出資料更新
		else if (BuySell.SELL == buySell) {
			existData.sellUpdateExistDataBtc(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to update treasury currency: <{}>...", existData);
			treasuryCryptoCurrencyRepo.save(existData);
			logger.info("<<<<< Update treasury currency: <{}> done, time-spent: <{} ms>", existData, (System.currentTimeMillis() - startTime));
		}
	}

	/**
	 * 刪除虛擬貨幣買賣紀錄, 不開放給使用者使用
	 */
	public String deleteCryptoCurrencyBuySellRecord(String userId, String currencyCode, String dateTime, BuySell buySell) {
		String id = CryptoCurrencyBSRecord.getId(userId, currencyCode, dateTime, buySell);
		
		logger.info(">>>>> Prepare to delete crypto currency buy sell record with id: {}...", id);
		cryptoCurrencyBSRecordRepo.delete(id);
		logger.info("<<<<< Delete crypto currency buy sell record with id: {} done", id);
		
		return "刪除 " + dateTime + " " + buySell + " " + currencyCode + " 資訊成功";
	}

	/**
	 * 刪除虛擬貨幣庫存紀錄, 不開放給使用者使用
	 */
	public String deleteTreasuryCryptoCurrency(String userId, String currencyCode) {
		String id = TreasuryCryptoCurrency.getId(userId, currencyCode);

		logger.info(">>>>> Prepare to delete treasury crypto currency with id: {}...", id);
		treasuryCryptoCurrencyRepo.delete(id);
		logger.info("<<<<< Delete treasury crypto currency with id: {} done", id);
		
		return "刪除 " + currencyCode + " 庫存資訊成功";
	}

	/**
	 * <pre>
	 * 查詢虛擬貨幣庫存
	 * </pre>
	 * 
	 * @param userId
	 * @return
	 */
	public String queryTreasuryCryptoCurrency(String userId) {
		List<TreasuryCryptoCurrency> treasuryCryptoCurrencys = treasuryCryptoCurrencyRepo.findByUserId(userId);
		if (treasuryCryptoCurrencys == null || treasuryCryptoCurrencys.isEmpty()) {
			return "您無虛擬貨幣庫存紀錄";
		}
		
		boolean appendResult = true;
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < treasuryCryptoCurrencys.size(); i++) {
			TreasuryCryptoCurrency treasuryCryptoCurrency = treasuryCryptoCurrencys.get(i);
			
			String currencyCode = treasuryCryptoCurrency.getCurrencyCode();
			CurrencyCnst currency = CurrencyCnst.convert(currencyCode);
			if (currency == CurrencyCnst.BTC) {
				appendResult = appendBtcTreasury(buffer, treasuryCryptoCurrency); 
				if (!appendResult) {
					return buffer.toString();
				}
			}
			else {
				appendResult = appendNonBtcTreasury(buffer, treasuryCryptoCurrency);
				if (!appendResult) {
					return buffer.toString();
				}
			}
			
			if (i != treasuryCryptoCurrencys.size() - 1) {
				buffer.append("\n=======================\n");
			} 
		}
		
		return buffer.toString();
	}
	
	/**
	 * <pre>
	 * 處理 BTC 的庫存資訊
	 * </pre>
	 * 
	 * @param buffer
	 * @param treasuryCryptoCurrency
	 * @return
	 */
	private boolean appendBtcTreasury(StringBuilder buffer, TreasuryCryptoCurrency treasuryCryptoCurrency) {
		final DecimalFormat usdFormat = new DecimalFormat("#.00");
		final DecimalFormat twdFormat = new DecimalFormat("#");

		String currencyCode = treasuryCryptoCurrency.getCurrencyCode();
		double totalVolumes = treasuryCryptoCurrency.getTotalVolumes();
		
		buffer.append("[").append(currencyCode.toUpperCase()).append("]\n");
		buffer.append("總數量: ").append(totalVolumes);
		
		if (totalVolumes != 0) {
			BigDecimal btcUsdRate = null;
			try {
				btcUsdRate = getCryptoLastPriceFromBtcE(CurrencyPair.BTC_USD);
			} catch (Exception e) {
				logger.error("Get BTC/USD from BTC-E got exception, please check...", e);
				buffer.append("\n從 BTC-E 取得 BTC/USD 匯率失敗, 請通知系統管理員");
				return false;
			}
			BigDecimal usdAmount = new BigDecimal(String.valueOf(totalVolumes)).multiply(btcUsdRate);
			buffer.append("\n賣出可得金額(USD): ").append(usdFormat.format(usdAmount));
			
			BigDecimal usdTwdRate = null;
			try {
				usdTwdRate = getBuyCashRatesFromTaiwanBank(CurrencyCnst.USD);
			} catch (Exception e) {
				logger.error("Get USD/TWD from Taiwan Bank got exception, please check...", e);
				buffer.append("\n從中央銀行取得 USD/TWD 匯率失敗, 請通知系統管理員");
				return false;
			}
			BigDecimal twdAmount = usdAmount.multiply(usdTwdRate);
			buffer.append("\n賣出可得金額(TWD): ").append(twdFormat.format(twdAmount));
			
			buffer.append("\nBTC/USD (參考 BTC-E): ").append(btcUsdRate);
			buffer.append("\nUSD/TWD (參考台灣銀行現金買入): ").append(usdTwdRate);
		}
		
		return true;
	}

	/**
	 * <pre>
	 * 處理非 BTC 的庫存資訊
	 * </pre>
	 * 
	 * @param buffer
	 * @param treasuryCryptoCurrency
	 * @return
	 */
	private boolean appendNonBtcTreasury(StringBuilder buffer, TreasuryCryptoCurrency treasuryCryptoCurrency) {
		String currencyCode = treasuryCryptoCurrency.getCurrencyCode();
		double avgPrice = treasuryCryptoCurrency.getAvgPrice();
		double totalVolumes = treasuryCryptoCurrency.getTotalVolumes();
		double btcAmount = treasuryCryptoCurrency.getAmount();
		
		if (totalVolumes != 0) {
			CurrencyPair currencyPair = getCurrencyPairByCurrencyCode(currencyCode);
			if (currencyPair == null) {
				logger.error("Get CurrencyPair by currencyCode: <{}> is null, please check...", currencyCode);
				buffer.append("\n不支援紀錄的虛擬貨幣, 請通知系統管理員");
				return false;
			}
			
			BigDecimal lastPrice = null;
			try {
				lastPrice = getCryptoLastPriceFromPoloneix(currencyPair);
			} catch (Exception e) {
				logger.error("Get Last Price from Poloneix by CurrencyPair: <{}> got exception, please check...", currencyPair, e);
				buffer.append("\n從 Poloneix 取得 ").append(currencyPair).append(" 匯率失敗, 請通知系統管理員");
				return false;
			}
			
			BigDecimal btcUsdRate = null;
			try {
				btcUsdRate = getCryptoLastPriceFromBtcE(CurrencyPair.BTC_USD);
			} catch (Exception e) {
				logger.error("Get BTC/USD from BTC-E got exception, please check...", e);
				buffer.append("\n從 BTC-E 取得 BTC/USD 匯率失敗, 請通知系統管理員");
				return false;
			}
	
			BigDecimal usdTwdRate = null;
			try {
				usdTwdRate = getBuyCashRatesFromTaiwanBank(CurrencyCnst.USD);
			} catch (Exception e) {
				logger.error("Get USD/TWD from Taiwan Bank got exception, please check...", e);
				buffer.append("\n從台灣銀行取得 USD/TWD 匯率失敗, 請通知系統管理員");
				return false;
			}
	
			buffer.append("[").append(currencyCode.toUpperCase()).append("]");
			buffer.append("\n均價(BTC): ").append(cryptoCurrencyDecFormat.format(avgPrice));
			buffer.append("\n總數量: ").append(totalVolumes);
			buffer.append("\n總金額(BTC): ").append(btcAmount);
	
			BigDecimal twdAmount = new BigDecimal(String.valueOf(btcAmount)).multiply(btcUsdRate).multiply(usdTwdRate);
			buffer.append("\n總金額(TWD): ").append(twdFormat.format(twdAmount));
	
			buffer.append("\n--------------------");
	
			buffer.append("\n目前成交價(BTC): ").append(cryptoCurrencyDecFormat.format(lastPrice));
			buffer.append("\n--------------------");
	
			BigDecimal sellRightNowBtcAmount = lastPrice.multiply(new BigDecimal(String.valueOf(totalVolumes)));
			buffer.append("\n賣出可得金額(BTC): ").append(cryptoCurrencyDecFormat.format(sellRightNowBtcAmount));
	
			BigDecimal btcWinLoseAmount = sellRightNowBtcAmount.subtract(new BigDecimal(String.valueOf(btcAmount)));
			buffer.append("\n損益試算(BTC): ").append(cryptoCurrencyDecFormat.format(btcWinLoseAmount));
	
			buffer.append("\n--------------------");
				
			BigDecimal sellRightNowTwdAmount = sellRightNowBtcAmount.multiply(btcUsdRate).multiply(usdTwdRate);
			buffer.append("\n賣出可得金額(TWD): ").append(twdFormat.format(sellRightNowTwdAmount));
			
	
			BigDecimal twdWinLoseAmount = btcWinLoseAmount.multiply(btcUsdRate).multiply(usdTwdRate);
			buffer.append("\n損益試算(TWD): ").append(twdFormat.format(twdWinLoseAmount));
			
			buffer.append("\n--------------------");
	
			buffer.append("\nBTC/USD (參考 BTC-E): ").append(btcUsdRate);
			buffer.append("\nUSD/TWD (參考台灣銀行現金買入): ").append(usdTwdRate);
		}
		
		return true;
	}

	private CurrencyPair getCurrencyPairByCurrencyCode(String currencyCode) {
		if (currencyCode.equalsIgnoreCase(CurrencyCnst.BTC.toString())) {
			return CurrencyPair.BTC_USD;
		}
		else if (currencyCode.equalsIgnoreCase(CurrencyCnst.STR.toString())) {
			return CurrencyPair.STR_BTC;
		}
		else if (currencyCode.equalsIgnoreCase(CurrencyCnst.XRP.toString())) {
			return CurrencyPair.XRP_BTC;
		}
		return null;
	}
	
	/**
	 * 還原預設值, 測試用 
	 */
	public void resetTreasury() {
		List<TreasuryCryptoCurrency> treasuryCryptoCurrencys = treasuryCryptoCurrencyRepo.findByUserId("U8e1ad9783b416aa040e54575e92ef776");
		for (TreasuryCryptoCurrency treasuryCryptoCurrency : treasuryCryptoCurrencys) {
			String currencyCode = treasuryCryptoCurrency.getCurrencyCode();
			if (currencyCode.equals("BTC")) {
				treasuryCryptoCurrency.setAvgPrice(0);
				treasuryCryptoCurrency.setTotalVolumes(0.00000459d);
				treasuryCryptoCurrency.setAmount(0);
			}
			else {
				treasuryCryptoCurrency.setAvgPrice(0);
				treasuryCryptoCurrency.setTotalVolumes(0);
				treasuryCryptoCurrency.setAmount(0);
			}
			treasuryCryptoCurrencyRepo.save(treasuryCryptoCurrency);
		}
	}
}
