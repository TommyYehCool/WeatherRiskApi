package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
	public void test_01_reset_original_data() {
		resetOriginalData();
		System.out.println("test_01_deleteTestingData done");
	}
	
	private void resetOriginalData() {
		currencyService.resetTreasury();
		
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:30", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:31", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/09-04:08:46", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/09-23:33:29", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/22-03:42:24", BuySell.SELL);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/23-23:25:35", BuySell.BUY);

		currencyService.deleteTreasuryCryptoCurrency(userId, "STR");
		
		String text = "";
		
		text = "2017/05/08-08:07:30 買貨幣 STR 0.00003800 12147.70723887";
		sendLineMsg(text);
		
		text = "2017/05/08-08:07:31 買貨幣 STR 0.00003800 5852.29276113";
		sendLineMsg(text);

		text = "2017/05/09-04:08:46 買貨幣 STR 0.00001900 1480";
		sendLineMsg(text);
		
		text = "2017/05/09-23:33:29 買貨幣 STR 0.00001800 17";
		sendLineMsg(text);
		
		text = "2017/05/22-03:42:24 賣貨幣 STR 0.00003281 19467.7545";
		sendLineMsg(text);
	}
	
	@Test
	public void test_02_sendBuySellCurrency() {
		String text = "";
		
		text = "2017/05/23-23:25:35 買貨幣 STR 0.00002541 674.72530499";
		sendLineMsg(text);
	}
	
	@Test
	@Ignore
	public void test_03_queryTreasuryCurrency() {
		String text = "查詢貨幣庫存";
		sendLineMsg(text);
	}
	
	// 測試 templateMsg
	@Test
	@Ignore
	public void test_04_testTemplateMsg() {
		String text = "coin";
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
