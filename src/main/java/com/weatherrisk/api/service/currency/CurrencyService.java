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
import com.weatherrisk.api.cnst.CryptoCurrencyExchange;
import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.model.currency.CryptoCurrencyBSRecord;
import com.weatherrisk.api.model.currency.CryptoCurrencyBSRecordRepository;
import com.weatherrisk.api.model.currency.TreasuryCryptoCurrency;
import com.weatherrisk.api.model.currency.TreasuryCryptoCurrencyRepository;

@Service
public class CurrencyService {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyService.class);
	
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
		buffer.append("(備註: 美金對台幣匯率, 參考台灣銀行現金買入)\n");
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
		buffer.append("最高價 ").append(baseCurrency).append("/USD: ").append(ticker.getHigh()).append("\n");
		buffer.append("最低價 ").append(baseCurrency).append("/USD: ").append(ticker.getLow()).append("\n");
		buffer.append("BTC/USD: ").append(btcUsdRate).append("\n");
		buffer.append("USD/TWD: ").append(usdTwdRate).append("\n");
		buffer.append("(備註: BTC對美金匯率, 參考 BTC-E)\n");
		buffer.append("(備註: 美金對台幣匯率, 參考台灣銀行現金買入)\n");
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

		// 回傳訊息
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(buyDateTime);
		buffer.append(" 買進 (").append(currencyCode).append(")");
		buffer.append(" $").append(decFormat.format(buyPrice)).append(" ");
		buffer.append(buyVolumes).append("顆").append(", 資訊儲存成功");

		return buffer.toString();
	}

	/**
	 * 新增虛擬貨幣買賣紀錄
	 * 
	 * @param userId
	 * @param currencyCode
	 * @param buySell
	 * @param buyDateTime
	 * @param buyPrice
	 * @param buyVolumes
	 * @return
	 * @throws ParseException
	 */
	private CryptoCurrencyBSRecord addBuySellRecord(String userId, String currencyCode, BuySell buySell, String buyDateTime, BigDecimal buyPrice, BigDecimal buyVolumes) throws ParseException {
		CryptoCurrencyBSRecord currencyRecord = new CryptoCurrencyBSRecord();
		try {
			currencyRecord.setData(userId, currencyCode, buySell, buyDateTime, buyPrice, buyVolumes);
		} catch (ParseException e) {
			logger.error("Exception raised while paring buyDateTime, the correct format is 'yyyy/MM/dd-HH:mm:ss'");
			throw e;
		}
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to insert currency record, {}...", currencyRecord);
		cryptoCurrencyBSRecordRepo.insert(currencyRecord);
		logger.info("<<<<< Insert currency record done, time-spent: <{} ms>", (System.currentTimeMillis() - startTime));
		
		return currencyRecord;
	}

	/**
	 * 更新虛擬貨幣庫存紀錄
	 * 
	 * @param bsRecord
	 */
	private void updateTreasuryCurrency(CryptoCurrencyBSRecord bsRecord) {
		String userId = bsRecord.getUserId();
		String currencyCode = bsRecord.getCurrencyCode();
		
		String id = TreasuryCryptoCurrency.getId(userId, currencyCode);
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to get treasury currency by id: <{}>...", id);
		TreasuryCryptoCurrency existData = treasuryCryptoCurrencyRepo.findOne(id);
		logger.info("<<<<< Get treasury currency by id: <{}> done, time-spent: <{} ms>", id, (System.currentTimeMillis() - startTime));
		
		// 代表全新, 要新增
		if (existData == null) {
			TreasuryCryptoCurrency newData = new TreasuryCryptoCurrency();
			newData.setNewData(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to insert treasury currency: <{}>...", newData);
			treasuryCryptoCurrencyRepo.insert(newData);
			logger.info("<<<<< Insert treasury currency: <{}> done, time-spent: <{} ms>", newData, (System.currentTimeMillis() - startTime));
		}
		// 代表已有資料要更新
		else {
			existData.buyUpdateExistData(bsRecord);
			
			startTime = System.currentTimeMillis();
			logger.info(">>>>> Prepare to update treasury currency: <{}>...", existData);
			treasuryCryptoCurrencyRepo.save(existData);
			logger.info("<<<<< Update treasury currency: <{}> done, time-spent: <{} ms>", existData, (System.currentTimeMillis() - startTime));
		}
	}

	public String addSellCryptoCurrency(String userId, String sellDateTime, String currencyCode, BigDecimal sellPrice, BigDecimal sellVolumes) {
		// TODO Auto-generated method stub
		return null;
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

	public String deleteTreasuryCryptoCurrency(String userId, String currencyCode) {
		String id = TreasuryCryptoCurrency.getId(userId, currencyCode);

		logger.info(">>>>> Prepare to delete treasury crypto currency with id: {}...", id);
		treasuryCryptoCurrencyRepo.delete(id);
		logger.info("<<<<< Delete treasury crypto currency with id: {} done", id);
		
		return "刪除 " + currencyCode + " 庫存資訊成功";
	}

	public String queryTreasuryCryptoCurrency(String userId) {
		DecimalFormat decFormat = new DecimalFormat("###0.00000000");
		
		List<TreasuryCryptoCurrency> treasuryCryptoCurrencys = treasuryCryptoCurrencyRepo.findByUserId(userId);
		if (treasuryCryptoCurrencys == null || treasuryCryptoCurrencys.isEmpty()) {
			return "您無虛擬貨幣庫存紀錄";
		}
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < treasuryCryptoCurrencys.size(); i++) {
			TreasuryCryptoCurrency treasuryCryptoCurrency = treasuryCryptoCurrencys.get(i);
			
			String currencyCode = treasuryCryptoCurrency.getCurrencyCode();
			double avgPrice = treasuryCryptoCurrency.getAvgPrice();
			double totalVolumes = treasuryCryptoCurrency.getTotalVolumes();
			double amount = treasuryCryptoCurrency.getAmount();
			
			buffer.append("[").append(currencyCode.toUpperCase()).append("]\n");
			buffer.append("均價(BTC): ").append(decFormat.format(avgPrice)).append("\n");
			buffer.append("總數量: ").append(totalVolumes).append("\n");
			buffer.append("總金額(BTC): ").append(amount);
			
			CurrencyPair currencyPair = getCurrencyPairByCurrencyCode(currencyCode);
			if (currencyPair == null) {
				logger.error("Get CurrencyPair by currencyCode: <{}> is null, please check...", currencyCode);
				return "不支援紀錄的虛擬貨幣, 請通知系統管理員";
			}
			
			BigDecimal lastPrice = null;
			try {
				lastPrice = getCryptoLastPriceFromPoloneix(currencyPair);
			} catch (Exception e) {
				logger.error("Get Last Price from Poloneix by CurrencyPair: <{}> got exception, please check...", currencyPair, e);
			}
			
			if (lastPrice != null) {
				buffer.append("\n目前成交價(BTC): ").append(decFormat.format(lastPrice.doubleValue())).append("\n");
				
				BigDecimal currentSellMatchAmount = lastPrice.multiply(new BigDecimal(totalVolumes));
				buffer.append("賣出可得金額(BTC): ").append(currentSellMatchAmount.doubleValue()).append("\n");
				
				BigDecimal btcWinLoseAmount = currentSellMatchAmount.subtract(new BigDecimal(amount));
				buffer.append("損益試算(BTC): ").append(decFormat.format(btcWinLoseAmount.doubleValue()));
				
				BigDecimal btcUsdRate = null;
				try {
					btcUsdRate = getCryptoLastPriceFromBtcE(CurrencyPair.BTC_USD);
				} catch (Exception e) {
					logger.error("Get BTC Last Price from BTC-E got exception, please check...", e);
				}

				BigDecimal usdTwdRate = null;
				try {
					usdTwdRate = getBuyCashRatesFromTaiwanBank(CurrencyCnst.USD);
				} catch (Exception e) {
					logger.error("Get USD/TWD Rate from Taiwan Bank got exception, please check...", e);
				}
				
				if (btcUsdRate != null && usdTwdRate != null) {
					DecimalFormat twdFormat = new DecimalFormat("#");
					buffer.append("\n損益試算(TWD): ").append(twdFormat.format(btcWinLoseAmount.multiply(btcUsdRate).multiply(usdTwdRate))).append("\n");
					buffer.append("BTC/USD: ").append(btcUsdRate).append("\n");
					buffer.append("USD/TWD: ").append(usdTwdRate).append("\n");
					buffer.append("(備註: BTC對美金匯率, 參考 BTC-E)\n");
					buffer.append("(備註: 美金對台幣匯率, 參考台灣銀行現金買入)\n");
				}
			}
			
			if (i != treasuryCryptoCurrencys.size() - 1) {
				buffer.append("\n----------------\n");
			} 
		}
		
		return buffer.toString();
	}
	
	private CurrencyPair getCurrencyPairByCurrencyCode(String currencyCode) {
		if (currencyCode.equalsIgnoreCase(CurrencyCnst.STR.toString())) {
			return CurrencyPair.STR_BTC;
		}
		else if (currencyCode.equalsIgnoreCase(CurrencyCnst.XRP.toString())) {
			return CurrencyPair.XRP_BTC;
		}
		return null;
	}
}
