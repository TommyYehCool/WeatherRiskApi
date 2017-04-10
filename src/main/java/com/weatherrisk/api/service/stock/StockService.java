package com.weatherrisk.api.service.stock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.StockType;
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
	
	public void refreshStockInfo() {
		try {
			getTseStockInfo();
			getOtcStockInfo();
		} catch (Exception e) {
			logger.error("Exception raised while refresh TSE and OTC stock info", e);
		}
	}
	
	private String[] getExChAndNameByName(String name) {
		TseStock tseStock = tseStockRepo.findByName(name);
		if (tseStock != null) {
			return new String[] {"tse_" + tseStock.getId() + ".tw", tseStock.getName()};
		}
		OtcStock otcStock = otcStockRepo.findByName(name);
		if (otcStock != null) {
			return new String[] {"otc_" + otcStock.getId() + ".tw", otcStock.getName()};
		}
		return null;
	}
	
	private String[] getExChAndNameById(String id) {
		TseStock tseStock = tseStockRepo.findById(id);
		if (tseStock != null) {
			return new String[] {"tse_" + id + ".tw", tseStock.getName()};
		}
		OtcStock otcStock = otcStockRepo.findById(id);
		if (otcStock != null) {
			return new String[] {"otc_" + id + ".tw", otcStock.getName()};
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String getStockPriceByNameOrId(String stockNameOrId) {
		boolean isName = false;
		try {
			Integer.parseInt(stockNameOrId);
		}
		catch (Exception e) {
			isName = true;
		}
		
		String[] exChAndName = null;
		if (isName) {
			exChAndName = getExChAndNameByName(stockNameOrId);
		}
		else {
			exChAndName = getExChAndNameById(stockNameOrId);
		}
		
		if (exChAndName == null) {
			return "你輸入的代號: " + stockNameOrId + ", 找不到對應資料";
		}
		
		String ex_ch = exChAndName[0];
		String name = exChAndName[1];
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			
			String getSessionUrl = stockConfig.getSessionIdUrl();
			HttpGet request = new HttpGet(getSessionUrl);
			HttpResponse response = client.execute(request);
	
			String setCookie = response.getFirstHeader("Set-Cookie").getValue();
			String jsessionId = setCookie.split("; ")[0];
			
			String getPriceUrl = stockConfig.getPriceUrl(ex_ch);
			
			request = new HttpGet(getPriceUrl);
			request.addHeader("Cookie", jsessionId);
			
			response = client.execute(request);
			
			HttpEntity entity = response.getEntity();
			
			String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			json = json.replaceAll("\r\n", "").trim();

			Map<String, ?> map = new HashMap<>();

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				map = objectMapper.readValue(json, HashMap.class);
			} catch (IOException e) {
				logger.error("IOException raised while read json string to hashmap", e);
				return "抓取股票價格失敗";
			}
			
			List<?> msgArray = (List<?>) map.get("msgArray");
			Map<?, ?> dataMap = (Map<?, ?>) msgArray.get(0);
			
			String high = (String) dataMap.get("h");
			String low = (String) dataMap.get("l");
			String match = (String) dataMap.get("z");
			
			StringBuilder buffer = new StringBuilder();
			buffer.append(name).append("\n");
			buffer.append("最高價: ").append(high).append("\n");
			buffer.append("成交價: ").append(match).append("\n");
			buffer.append("最低價: ").append(low).append("\n");
			
			return buffer.toString();
		} catch (Exception e) {
			logger.error("Exception raised while get newest stock price", e);
			return "抓取最新股價失敗";
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
