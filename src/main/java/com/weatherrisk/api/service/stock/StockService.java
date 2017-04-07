package com.weatherrisk.api.service.stock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tomcat.util.net.SocketEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.config.stock.StockConfig;
import com.weatherrisk.api.model.stock.OtcStock;
import com.weatherrisk.api.model.stock.OtcStockRepository;
import com.weatherrisk.api.model.stock.Stock;
import com.weatherrisk.api.model.stock.TseStock;
import com.weatherrisk.api.model.stock.TseStockRepository;

@Service
public class StockService {
	
	private Logger logger = LoggerFactory.getLogger(StockService.class);

	@Autowired
	private StockConfig stockConfig;
	
	@Autowired
	private TseStockRepository tseStockRepo;
	
	@Autowired
	private OtcStockRepository otcStockRepo;
	
	private enum StockType {
		TSE, OTC;
	}
	
	public void refreshStockInfo() {
		try {
			getTseStockInfo();
			getOtcStockInfo();
		} catch (Exception e) {
			logger.error("Exception raised while refresh TSE and OTC stock info", e);
		}
	}

	private void getTseStockInfo() throws Exception {
		StockType stockType = StockType.TSE;

		List<TseStock> tseStocks = getStockInfo(stockType, stockConfig.getTseStockInfoUrl());
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to delete all {} stocks infomation...", stockType);
		tseStockRepo.deleteAll();
		logger.info("<<<<< Delete all {} stocks infomation done, time-spent: <{} ms>", stockType, (System.currentTimeMillis() - startTime));
		
		startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to insert all {} stocks infomation...", stockType);
		tseStockRepo.insert(tseStocks);
		logger.info("<<<<< Insert all {} stocks infomation done, time-spent: <{} ms>", stockType, (System.currentTimeMillis() - startTime));
	}

	private void getOtcStockInfo() throws Exception {
		StockType stockType = StockType.OTC;

		List<OtcStock> otcStocks = getStockInfo(stockType, stockConfig.getOtcStockInfoUrl());
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to delete all {} stocks infomation...", stockType);
		otcStockRepo.deleteAll();
		logger.info("<<<<< Delete all {} stocks infomation done, time-spent: <{} ms>", stockType, (System.currentTimeMillis() - startTime));
		
		startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to insert all {} stocks infomation...", stockType);
		otcStockRepo.insert(otcStocks);
		logger.info("<<<<< Insert all {} stocks infomation done, time-spent: <{} ms>", stockType, (System.currentTimeMillis() - startTime));
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Stock> List<T> getStockInfo(StockType stockType, String url) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to get {} all stocks infomation from url: <{}>...", stockType, url);
		
		boolean isTse = stockType == StockType.TSE;
		
		List<T> results = new ArrayList<>();
		
		Document document 
			= Jsoup.connect(url)
				.header("Accept-Encoding", "gzip, deflate")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
				.maxBodySize(0)
				.timeout(600000)
				.get();

		Elements elmTableBody = document.select("table.h4 > tbody");
		Elements elmTrs = elmTableBody.select("tr");
	
		Iterator<Element> itElmTrs = elmTrs.iterator();
	
		while (itElmTrs.hasNext()) {
			Element elmTr = itElmTrs.next();

			String firstTdText = elmTr.select("td").first().text();
		
			String[] splitBySpace = firstTdText.split("　");
			if (splitBySpace.length != 2) {
				continue;
			}
			
			String id = splitBySpace[0];
			String name = splitBySpace[1];
		
			Stock stock;
			if (isTse) {
				stock = new TseStock();
			}
			else {
				stock = new OtcStock();
			}
			
			stock.setId(id);
			stock.setName(name);
			
			results.add((T) stock);
		}
		
		logger.info("<<<<< Get {} all stocks infomation from url: <{}> done, time-spent: <{} ms>", stockType, url, (System.currentTimeMillis() - startTime));
		
		return results;
	}
}
