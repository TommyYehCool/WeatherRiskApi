package com.weatherrisk.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.service.PoloniexTradeService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.knowm.xchange.utils.CertHelper;

public class Test_PoloniexTrade {

	private static final String API_KEY 
		= "Your API Key";
	private static final String SECRET_KEY 
		= "Your Secret Key";

	private static CurrencyPair currencyPair = new CurrencyPair(Currency.STR, Currency.BTC);
	private static BigDecimal str_price = new BigDecimal("0.00001000");
	private static BigDecimal amount = new BigDecimal("2000");

	public static Exchange getExchange() {
		ExchangeSpecification spec = new ExchangeSpecification(PoloniexExchange.class);
		spec.setApiKey(API_KEY);
		spec.setSecretKey(SECRET_KEY);
		return ExchangeFactory.INSTANCE.createExchange(spec);
	}

	public static void main(String[] args) throws Exception {
		CertHelper.trustAllCerts();

		Exchange poloniex = getExchange();
		TradeService tradeService = poloniex.getTradeService();

		queryTradeHistory(tradeService);
		
		String orderId = buy(tradeService);
		
		cancelOrder(tradeService, orderId);
	}

	private static void queryTradeHistory(TradeService tradeService) throws IOException {
		System.out.println("---------- Query Trade History ----------");
		
		System.out.println(">>>>> Prepare to query trade history with CurrencyPair: <" + currencyPair + ">");
		PoloniexTradeService.PoloniexTradeHistoryParams params = new PoloniexTradeService.PoloniexTradeHistoryParams();
		params.setCurrencyPair(currencyPair);
		System.out.println(tradeService.getTradeHistory(params));
	
		Date startTime = new Date();
		System.out.println(">>>>> Prepare to query trade history with CurrencyPair: <" + currencyPair + ">, StartTime: <" + startTime + ">");
		params.setStartTime(startTime);
		System.out.println(tradeService.getTradeHistory(params));
	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 4);
		Date endTime = cal.getTime();
		System.out.println(">>>>> Prepare to query trade history with CurrencyPair: <" + currencyPair + ">, StartTime: <" + startTime + ">, EndTime: <" + endTime + ">");
		params.setEndTime(endTime);
		System.out.println(tradeService.getTradeHistory(params));
	}

	private static String buy(TradeService tradeService) throws IOException, InterruptedException {
		System.out.println("---------- Place limit order ----------");
		
		LimitOrder order = new LimitOrder.Builder(OrderType.BID, currencyPair).tradableAmount(amount).limitPrice(str_price).build();
		String orderId = tradeService.placeLimitOrder(order);

		System.out.println("Placed order #" + orderId);
		
		Thread.sleep(3000); // wait for order to propagate
	
		showOpenOrders(tradeService);
		
		return orderId;
	}

	private static void cancelOrder(TradeService tradeService, String orderId) throws IOException, InterruptedException {
		System.out.println("---------- Cancel order ----------");
		
		boolean canceled = tradeService.cancelOrder(orderId);
		if (canceled) {
			System.out.println("Successfully canceled order #" + orderId);
		} else {
			System.out.println("Did not successfully cancel order #" + orderId);
		}

		Thread.sleep(3000); // wait for cancellation to propagate

		showOpenOrders(tradeService);
	}

	private static void showOpenOrders(TradeService tradeService) throws IOException {
		OpenOrdersParams params = new DefaultOpenOrdersParamCurrencyPair(currencyPair);
		OpenOrders openOrders = tradeService.getOpenOrders(params);
		System.out.println(openOrders);
	}
}
