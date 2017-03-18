package com.weatherrisk.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.CurrencyCnst;

@Service
public class CurrencyService {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyService.class);
	
	/**
	 * 從各大交易所取得指定需擬貨幣目前價格
	 */
	public String getCryptoCurrencyPriceFromExchanges(CurrencyPair currencyPair) {
		StringBuilder buffer = new StringBuilder();
		try {
			BigDecimal usdTwdRate = getBuyCashRatesFromTaiwanBank(CurrencyCnst.USD);
			
			if (currencyPair.equals(CurrencyPair.BTC_USD)) {
				getCryptoCurrencyPriceFromExchange(buffer, "BTC-E", BTCEExchange.class.getName(), CurrencyCnst.BTC, currencyPair, usdTwdRate);

				buffer.append("\n");
				
				getCryptoCurrencyPriceFromExchange(buffer, "Bitstamp", BitstampExchange.class.getName(), CurrencyCnst.BTC, currencyPair, usdTwdRate);
			}
			else if (currencyPair.equals(CurrencyPair.ETH_USD)) {
				getCryptoCurrencyPriceFromExchange(buffer, "BTC-E", BTCEExchange.class.getName(), CurrencyCnst.ETH, currencyPair, usdTwdRate);
			}
			else if (currencyPair.equals(CurrencyPair.LTC_USD)) {
				getCryptoCurrencyPriceFromExchange(buffer, "BTC-E", BTCEExchange.class.getName(), CurrencyCnst.LTC, currencyPair, usdTwdRate);
			}
			
			return buffer.toString();
		} catch (IOException e) {
			logger.error("IOException raised while trying to get price", e);
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
			CurrencyCnst baseCurrency, CurrencyPair currencyPair, BigDecimal usdTwdRate) throws IOException {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

		MarketDataService marketDataService = exchange.getMarketDataService();
		
		Ticker ticker = marketDataService.getTicker(currencyPair);
		
		DateFormat updateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DecimalFormat outDecFormat = new DecimalFormat("#.##");
		
		buffer.append(exchangeName).append(":\n");
		buffer.append("目前成交價 ").append(ticker.getCurrencyPair()).append(": ").append(ticker.getLast()).append("\n");
		buffer.append("換算台幣價 ").append(baseCurrency).append("/TWD: ").append(outDecFormat.format(usdTwdRate.multiply(ticker.getLast()))).append("\n");
		buffer.append("最高價 ").append(baseCurrency).append("/USD: ").append(ticker.getHigh()).append("\n");
		buffer.append("最低價 ").append(baseCurrency).append("/USD: ").append(ticker.getLow()).append("\n");
		buffer.append("更新時間: ").append(updateTimeFormat.format(ticker.getTimestamp())).append("\n");
		buffer.append("USD/TWD: ").append(usdTwdRate).append("\n");
		buffer.append("(備註: 美金對台幣匯率, 參考台灣銀行現金買入)\n");
	}
	
	/**
	 * 從 BTC E 取得某一檔虛擬貨幣的價格
	 * 
	 * @param currencyPair
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getCryptoLastPriceFromBtcE(CurrencyPair currencyPair) throws Exception {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName());

		MarketDataService marketDataService = exchange.getMarketDataService();
		
		Ticker ticker = marketDataService.getTicker(currencyPair);
		
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
			
		} catch (IOException e) {
			logger.error("IOException raised while trying to get price", e);
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
	private BigDecimal getBuyCashRatesFromTaiwanBank(CurrencyCnst currency) throws IOException {
		Map<String, ?> currencyRatesMap = getCurrencyRatesMapFromTaiwanBank(currency);
		
		String strBuyCash = (String) currencyRatesMap.get("buyCash");
		
		return new BigDecimal(strBuyCash); 
	}

	@SuppressWarnings("unchecked")
	private Map<String, ?> getCurrencyRatesMapFromTaiwanBank(CurrencyCnst currency)
			throws MalformedURLException, IOException, ProtocolException, JsonParseException, JsonMappingException {
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
	
}
