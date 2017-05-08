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
import com.weatherrisk.api.line.LineMsgHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_Line_MessageHandler {
	
	@Autowired
	private LineMsgHandler lineMsgHandler;

	@Test
	public void test_handleTextMessageEvent() {
		String msgId = "xxxxxxxxxxxxx";
		TextMessageContent msgContent = new TextMessageContent(msgId, "取消貨幣註冊 str");
		
		String userId = "U8e1ad9783b416aa040e54575e92ef776";
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

		MessageEvent<TextMessageContent> event = new MessageEvent<TextMessageContent>(null, src, msgContent, null);
		
		lineMsgHandler.handleTextMessageEvent(event);
	}
}
