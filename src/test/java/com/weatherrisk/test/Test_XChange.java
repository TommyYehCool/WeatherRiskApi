package com.weatherrisk.test;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bitstamp.dto.marketdata.BitstampTicker;
import org.knowm.xchange.bitstamp.service.BitstampMarketDataServiceRaw;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.btce.v3.dto.marketdata.BTCETickerWrapper;
import org.knowm.xchange.btce.v3.service.BTCEMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexTicker;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;
import org.knowm.xchange.service.marketdata.MarketDataService;

/**
 * 參考: <a href="https://github.com/timmolter/XChange/blob/develop/xchange-examples/src/main/java/org/knowm/xchange/examples/bitstamp/trade/BitstampTradeDemo.java">Bitstamp Trade Demo</a>
 * 
 * @author tommy.feng
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_XChange {

	@Test
	public void test_01_Bitstamp() throws IOException {
		// Use the factory to get Bitstamp exchange API using default settings
		Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());

		// Interested in the public market data feed (no authentication)
		MarketDataService marketDataService = bitstamp.getMarketDataService();

		CurrencyPair currencyPair = CurrencyPair.BTC_USD;

		showTicker(marketDataService, currencyPair);

		showSpecificTicker((BitstampMarketDataServiceRaw) marketDataService, currencyPair);
	}
	
	@Test
	public void test_02_BTCE() throws IOException {
		Exchange btce = ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName());
		
		MarketDataService marketDataService = btce.getMarketDataService();
		
		CurrencyPair currencyPair = CurrencyPair.BTC_USD;
		
		showTicker(marketDataService, currencyPair);
		
		showSpecificTicker((BTCEMarketDataServiceRaw) marketDataService);
	}
	
	@Test
	public void test_03_Poloniex() throws IOException {
		Exchange poloniex = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		
		MarketDataService marketDataService = poloniex.getMarketDataService();
		
		CurrencyPair currencyPair = CurrencyPair.STR_BTC;
		
		showTicker(marketDataService, currencyPair);
		
		showSpecificTicker((PoloniexMarketDataServiceRaw) marketDataService, currencyPair);
	}

	private void showTicker(MarketDataService marketDataService, CurrencyPair currencyPair) throws IOException {
		Ticker ticker = marketDataService.getTicker(currencyPair);

		System.out.println(ticker.toString());
	}

	private void showSpecificTicker(BitstampMarketDataServiceRaw marketDataService, CurrencyPair currencyPair) throws IOException {
		BitstampTicker bitstampTicker = marketDataService.getBitstampTicker(currencyPair);

		System.out.println(bitstampTicker.toString());
	}
	
	private void showSpecificTicker(BTCEMarketDataServiceRaw marketDataService) throws IOException {
		BTCETickerWrapper btceTicker = marketDataService.getBTCETicker("btc_usd");
		
		System.out.println(btceTicker.toString());
	}
	
	private void showSpecificTicker(PoloniexMarketDataServiceRaw marketDataService, CurrencyPair currencyPair) throws IOException {
		PoloniexTicker poloniexTicker = marketDataService.getPoloniexTicker(currencyPair);
		
		PoloniexMarketData marketData = poloniexTicker.getPoloniexMarketData();
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("[Poloniex]");
		buffer.append("\n").append(poloniexTicker.getCurrencyPair());
		buffer.append("\nLast: ").append(marketData.getLast());
		buffer.append("\nHighest Bid: ").append(marketData.getHighestBid()); // BUY
		buffer.append("\nLowest Ask: ").append(marketData.getLowestAsk()); // SELL
		buffer.append("\n24hr High: ").append(marketData.getHigh24hr());
		buffer.append("\n24hr Low: ").append(marketData.getLow24hr());
		
		System.out.println(buffer.toString());
	}
}
