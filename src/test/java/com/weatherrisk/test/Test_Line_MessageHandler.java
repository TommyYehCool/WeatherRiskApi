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
	@Ignore
	public void test_01_addBuyCurrenct() {
		deleteTestingData();
		
		sendAddBuyCurrency();
	}
	
	@Test
	public void test_02_queryTreasuryCurrency() {
		String text = "查詢貨幣庫存";
		sendLineMsg(text);
	}

	private void deleteTestingData() {
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:30", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/08-08:07:31", BuySell.BUY);
		currencyService.deleteCryptoCurrencyBuySellRecord(userId, "STR", "2017/05/09-04:08:46", BuySell.BUY);
		currencyService.deleteTreasuryCryptoCurrency(userId, "STR");
	}

	private void sendAddBuyCurrency() {
		String text = "";
		
		text = "2017/05/08-08:07:30 買貨幣 STR 0.00003800 12147.70723887";
		sendLineMsg(text);
		
		text = "2017/05/08-08:07:31 買貨幣 STR 0.00003800 5852.29276113";
		sendLineMsg(text);

		text = "2017/05/09-04:08:46 買貨幣 STR 0.00001900 1480";
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
