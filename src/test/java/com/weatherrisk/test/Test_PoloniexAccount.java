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
	
	private static final String API_KEY = "Your Api Key";
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
		
		Map<String, Wallet> wallets = accountInfo.getWallets();
		wallets.values().forEach(wallet -> {
			Map<Currency, Balance> balances = wallet.getBalances();
			balances.forEach((currency, balance) -> {
				if (balance.getAvailableForWithdrawal().doubleValue() != 0) {
					System.out.println("[" + currency + "] Available for withdrawal: " + balance.getAvailableForWithdrawal().doubleValue());
				}
				if (balance.getFrozen().doubleValue() != 0) {
					System.out.println("[" + currency + "] Frozen: " + balance.getFrozen().doubleValue());
				}
			});
		});
	}

	public static Exchange getExchange() {
		ExchangeSpecification spec = new ExchangeSpecification(PoloniexExchange.class);
		spec.setApiKey(API_KEY);
		spec.setSecretKey(SECRET_KEY);
		return ExchangeFactory.INSTANCE.createExchange(spec);
	}

}
