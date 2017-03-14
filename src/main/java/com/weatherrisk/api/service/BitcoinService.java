package com.weatherrisk.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BitcoinService {
	
	private Logger logger = LoggerFactory.getLogger(BitcoinService.class);
	
	/**
	 * 從各大交易所取得指定需擬貨幣目前價格
	 */
	public String getPriceFromExchanges(CurrencyPair currencyPair) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (currencyPair.equals(CurrencyPair.BTC_USD)) {
				getPriceFromExchange(buffer, "BTC-E", BTCEExchange.class.getName(), currencyPair);

				buffer.append("\n");
				
				getPriceFromExchange(buffer, "Bitstamp", BitstampExchange.class.getName(), currencyPair);
			}
			else if (currencyPair.equals(CurrencyPair.ETH_USD)) {
				getPriceFromExchange(buffer, "BTC-E", BTCEExchange.class.getName(), currencyPair);
			}
			
			return buffer.toString();
		} catch (IOException e) {
			logger.error("IOException raised while trying to get BTC price");
			return "抓取 BTC 價格失敗";
		}
	}
	
	/**
	 * <pre>
	 * 從指定交易所取得價格
	 * 
	 * 參考: <a href="https://github.com/timmolter/XChange/blob/develop/xchange-examples/src/main/java/org/knowm/xchange/examples/bitstamp/marketdata/BitstampTickerDemo.java">Bitstamp Ticker Demo</a>
	 * </pre>
	 */
	private void getPriceFromExchange(StringBuilder buffer, String exchangeName, String exchangeClassName, CurrencyPair currencyPair) throws IOException {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

		MarketDataService marketDataService = exchange.getMarketDataService();
		
		Ticker ticker = marketDataService.getTicker(currencyPair);
		
		DateFormat updateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		buffer.append(exchangeName).append(":\n");
		buffer.append(ticker.getCurrencyPair()).append(": ").append(ticker.getLast()).append("\n");
		buffer.append("最高價: ").append(ticker.getHigh()).append("\n");
		buffer.append("最低價: ").append(ticker.getLow()).append("\n");
		buffer.append("更新時間: ").append(updateTimeFormat.format(ticker.getTimestamp())).append("\n");
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
			reader.close();
		} catch (IOException e) {
			logger.error("IOException raised while trying to get Bitcoin price", e);
			return "抓取資料失敗";
		} finally {
			IOUtils.closeQuietly(reader);
		}

		Map<String, ?> map = new HashMap<>();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			map = objectMapper.readValue(srcBuffer.toString(), HashMap.class);
		} catch (IOException e) {
			logger.error("IOException raised while read json string to hashmap", e);
			return "抓取資料失敗";
		}
		
		DateFormat srcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		String timestamp = null;
		Date date = null;
		try {
			timestamp = (String) map.get("timestamp");
			date = srcDateFormat.parse(timestamp);
		} catch (ParseException e) {
			logger.error("ParseException raised while parsing timestamp to Date, timestamp: <{}>", timestamp, e);
			return "抓取資料失敗";
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
