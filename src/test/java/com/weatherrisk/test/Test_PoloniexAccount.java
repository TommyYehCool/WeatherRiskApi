package com.weatherrisk.test;

import java.math.BigDecimal;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.utils.CertHelper;

public class Test_PoloniexAccount {
	
	private static final String API_KEY = "Your API Key";
	private static final String SECRET_KEY = "Your Secret Key";
	

	public static void main(String[] args) throws Exception {
		CertHelper.trustAllCerts();

		Exchange poloniex = getExchange();
		AccountService accountService = poloniex.getAccountService();

		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println(accountInfo);

		String username = accountInfo.getUsername();
		System.out.println("Username: " + username);
		
		BigDecimal tradingFee = accountInfo.getTradingFee();
		System.out.println("Trading Fee: " + tradingFee);
		
		Wallet wallet = accountInfo.getWallet();
		Balance btcBalance = wallet.getBalance(Currency.BTC);
		BigDecimal availableForWithdrawal = btcBalance.getAvailableForWithdrawal();
		System.out.println("Available BTC for withdrawal: " + availableForWithdrawal.doubleValue());
	}

	public static Exchange getExchange() {
		ExchangeSpecification spec = new ExchangeSpecification(PoloniexExchange.class);
		spec.setApiKey(API_KEY);
		spec.setSecretKey(SECRET_KEY);
		return ExchangeFactory.INSTANCE.createExchange(spec);
	}

}
