package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.weatherrisk.api.Application;
import com.weatherrisk.api.cnst.BuySell;
import com.weatherrisk.api.line.LineMsgHandler;
import com.weatherrisk.api.service.currency.CurrencyService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_Line_MessageHandler {
	
	private final String userId = "U8e1ad9783b416aa040e54575e92ef776"; 
	
	@Autowired
	private LineMsgHandler lineMsgHandler;
	
	@Autowired
	private CurrencyService currencyService;

	@Test
	public void test_01_recreate() {
		currencyService.resetTreasury(0.00000459d);
		
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:30", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:31", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/09-04:08:46", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/09-23:33:29", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/22-03:42:24", BuySell.SELL);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/23-23:25:35", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/24-23:55:36", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-00:05:53", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-01:09:01", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-09:09:45", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-09:09:46", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-21:25:46", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "XRP", "2017/05/25-21:25:47", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/26-01:31:46", BuySell.BUY);

		currencyService.deleteTreasuryCryptoCurrency(userId, "STR");
		currencyService.deleteTreasuryCryptoCurrency(userId, "XRP");
		
		String text = "";
		
		text = "2017/05/08-08:07:30 買貨幣 STR 0.00003800 12147.70723887 0.15";
		sendLineMsg(text);
		
		text = "2017/05/08-08:07:31 買貨幣 STR 0.00003800 5852.29276113 0.15";
		sendLineMsg(text);

		text = "2017/05/09-04:08:46 買貨幣 STR 0.00001900 1480 0.15";
		sendLineMsg(text);
		
		text = "2017/05/09-23:33:29 買貨幣 STR 0.00001800 17 0.15";
		sendLineMsg(text);
		
		text = "2017/05/22-03:42:24 賣貨幣 STR 0.00003281 19467.7545 0.25";
		sendLineMsg(text);
		
		text = "2017/05/23-23:25:35 買貨幣 STR 0.00002541 674.72530499 0.15";
		sendLineMsg(text);
		
		text = "2017/05/24-23:55:36 買貨幣 XRP 0.0001365 489 0.15";
		sendLineMsg(text);
		
		text = "2017/05/25-00:05:53 買貨幣 XRP 0.0001335 398.88771535 0.15";
		sendLineMsg(text);
		
		text = "2017/05/25-01:09:01 買貨幣 XRP 0.00013 769.23084615 0.15";
		sendLineMsg(text);
		
		text = "2017/05/25-09:09:45 買貨幣 XRP 0.00011324 1.78128623 0.25";
		sendLineMsg(text);
		
		text = "2017/05/25-09:09:46 買貨幣 XRP 0.00011388 437.27745982 0.15";
		sendLineMsg(text);
		
		text = "2017/05/25-21:25:46 買貨幣 XRP 0.00010956 28.31502241 0.25";
		sendLineMsg(text);
		
		text = "2017/05/25-21:25:47 買貨幣 XRP 0.00010999 1335.33179577 0.25";
		sendLineMsg(text);
		
		text = "2017/05/26-01:31:46 買貨幣 STR 0.0000185 5405.4054054 0.15";
		sendLineMsg(text);

		System.out.println("test_01_recreate done");
	}
	
	@Test
	public void test_02_queryTreasuryCurrency() {
		String text = "查詢貨幣庫存";
		sendLineMsg(text);
	}
	
	@Test
	public void test_03_queryCryptoCurrencyPrice() {
		String text = "ltc";
		sendLineMsg(text);
	}
	
	private void sendLineMsg(String text) {
		Source src = getSource();
		String msgId = "xxxxxxxxxxxxx";
		TextMessageContent msgContent = new TextMessageContent(msgId, text);
		MessageEvent<TextMessageContent> event = new MessageEvent<TextMessageContent>(null, src, msgContent, null);
		lineMsgHandler.handleTextMessageEvent(event);
	}
	
	private Source getSource() {
		Source src = new Source() {
			@Override
			public String getUserId() {
				return userId;
			}
			
			@Override
			public String getSenderId() {
				return null;
			}
		};
		return src;
	}
}
