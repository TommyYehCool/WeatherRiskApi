package com.weatherrisk.api.line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.weatherrisk.api.service.ParkingLotService;

@LineMessageHandler
public class LineMsgHandler {
	private static final Logger logger = LoggerFactory.getLogger(LineMsgHandler.class);
	
	@Autowired
	private ParkingLotService parkingLotService;
	
    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	logger.info(">>>>> handle text message event, event: {}", event);

    	String inputMsg = event.getMessage().getText();
    	
    	// 精準搜尋
    	if (inputMsg.startsWith("#")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		String queryResult = parkingLotService.findByName(name);
    		return new TextMessage(queryResult);
    	}
    	// 模糊搜尋
    	else if (inputMsg.startsWith("@")) {
    		String name = inputMsg.substring(1, inputMsg.length());
    		String queryResult = parkingLotService.findByNameLike(name);
    		return new TextMessage(queryResult);
    	}
    	return new TextMessage(event.getMessage().getText());
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        logger.info(">>>>> handle default message event, event: {}", event);
    }
}
