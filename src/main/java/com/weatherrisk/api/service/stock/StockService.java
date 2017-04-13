package com.weatherrisk.api.service.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.weatherrisk.api.model.stock.TreasuryStock;
import com.weatherrisk.api.model.stock.TreasuryStockRepository;
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
	
	@Autowired
	private TreasuryStockRepository treasuryStockRepo;
	
	public void refreshStockInfo() {
		try {
			getTseStockInfo();
			getOtcStockInfo();
		} catch (Exception e) {
			logger.error("Exception raised while refreshing TSE and OTC stock info", e);
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

	public boolean isSupportedStock(String stockNameOrId) {
		boolean isSupportedStock = false;
		
		boolean isName = !StringUtils.isNumeric(stockNameOrId);

		if (isName) {
			TseStock tseStock = tseStockRepo.findByName(stockNameOrId);
			if (tseStock != null) {
				isSupportedStock = true;
			}
			else {
				OtcStock otcStock = otcStockRepo.findByName(stockNameOrId);
				if (otcStock != null) {
					isSupportedStock = true;
				}
			}
		}
		else {
			TseStock tseStock = tseStockRepo.findById(stockNameOrId);
			if (tseStock != null) {
				isSupportedStock = true;
			}
			else {
				OtcStock otcStock = otcStockRepo.findById(stockNameOrId);
				if (otcStock != null) {
					isSupportedStock = true;
				}
			}
		}
		return isSupportedStock;
	}
	
	public String getStockPriceStrByNameOrId(String stockNameOrId) {
		String[] exChAndName = getExChAndNameByNameOrId(stockNameOrId);
		if (exChAndName == null) {
			return "你輸入的代號: " + stockNameOrId + ", 找不到對應資料";
		}
		
		String ex_ch = exChAndName[0];
		String name = exChAndName[1];
		
		Map<?, ?> dataMap = null;
		try {
			dataMap = sendRequestToGetDataMap(ex_ch);
		} catch (Exception e) {
			logger.error("Exception raised while trying to get newest stock price", e);
			return "抓取最新股價失敗";
		}
			
		String high = (String) dataMap.get("h");
		String low = (String) dataMap.get("l");
		String match = (String) dataMap.get("z");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(name).append("\n");
		buffer.append("最高價: ").append(high).append("\n");
		buffer.append("成交價: ").append(match).append("\n");
		buffer.append("最低價: ").append(low).append("\n");
		
		return buffer.toString();
	}
	
	public BigDecimal getStockMatchPriceByNameOrId(String stockNameOrId) throws Exception {
		String[] exChAndName = getExChAndNameByNameOrId(stockNameOrId);
		if (exChAndName == null) {
			return null;
		}
		
		String ex_ch = exChAndName[0];
		
		Map<?, ?> dataMap = sendRequestToGetDataMap(ex_ch);
		
		String match = (String) dataMap.get("z");
		
		return new BigDecimal(match);
	}

	private String[] getExChAndNameByNameOrId(String stockNameOrId) {
		boolean isName = !StringUtils.isNumeric(stockNameOrId);
		if (isName) {
			TseStock tseStock = tseStockRepo.findByName(stockNameOrId);
			if (tseStock != null) {
				String ex_ch = "tse_" + tseStock.getId() + ".tw";
				String stockName = tseStock.getName() + " (" + tseStock.getId() + ")";
				return new String[] {ex_ch, stockName};
			}
			OtcStock otcStock = otcStockRepo.findByName(stockNameOrId);
			if (otcStock != null) {
				String ex_ch = "otc_" + otcStock.getId() + ".tw";
				String stockName = otcStock.getName() + " (" + otcStock.getId() + ")";
				return new String[] {ex_ch, stockName};
			}
		}
		else {
			TseStock tseStock = tseStockRepo.findById(stockNameOrId);
			if (tseStock != null) {
				String ex_ch = "tse_" + tseStock.getId() + ".tw";
				String stockName = tseStock.getName() + " (" + tseStock.getId() + ")";
				return new String[] {ex_ch, stockName};
			}
			OtcStock otcStock = otcStockRepo.findById(stockNameOrId);
			if (otcStock != null) {
				String ex_ch = "otc_" + otcStock.getId() + ".tw";
				String stockName = otcStock.getName() + " (" + otcStock.getId() + ")";
				return new String[] {ex_ch, stockName};
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Stock> T getStockByNameOrId(String stockNameOrId) {
		boolean isName = !StringUtils.isNumeric(stockNameOrId);
		if (isName) {
			TseStock tseStock = tseStockRepo.findByName(stockNameOrId);
			if (tseStock != null) {
				return (T) tseStock;
			}
			OtcStock otcStock = otcStockRepo.findByName(stockNameOrId);
			if (otcStock != null) {
				return (T) otcStock;
			}
		}
		else {
			TseStock tseStock = tseStockRepo.findById(stockNameOrId);
			if (tseStock != null) {
				return (T) tseStock;
			}
			OtcStock otcStock = otcStockRepo.findById(stockNameOrId);
			if (otcStock != null) {
				return (T) otcStock;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<?, ?> sendRequestToGetDataMap(String ex_ch) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		
		// 先發 request 取得 sessionId
		String getSessionUrl = stockConfig.getSessionIdUrl();
		HttpGet request = new HttpGet(getSessionUrl);
		HttpResponse response = client.execute(request);

		String setCookie = response.getFirstHeader("Set-Cookie").getValue();
		String jsessionId = setCookie.split("; ")[0];

		// 將取得的 sessionId 塞進 cookie, 打取得價錢的 url
		String getPriceUrl = stockConfig.getPriceUrl(ex_ch);
		request = new HttpGet(getPriceUrl);
		request.addHeader("Cookie", jsessionId);
		response = client.execute(request);
		
		HttpEntity entity = response.getEntity();
		
		String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		json = json.replaceAll("\r\n", "").trim();

		Map<String, ?> map = new HashMap<>();

		ObjectMapper objectMapper = new ObjectMapper();
		map = objectMapper.readValue(json, HashMap.class);
		
		List<?> msgArray = (List<?>) map.get("msgArray");
		Map<?, ?> dataMap = (Map<?, ?>) msgArray.get(0);
		
		return dataMap;
	}
	
	public String addBuyStock(String userId, String buyDate, String stockNameOrId, double buyPrice, long buyShares) {
		TreasuryStock buyStock = new TreasuryStock();
		buyStock.setUserId(userId);
		try {
			buyStock.setBuyDate(buyDate);
		} catch (ParseException e) {
			logger.error("Exception raised while paring buyDate, the correct format is 'yyyy/MM/dd'");
			return "新增失敗, 因日期格式錯誤";
		}

		Stock stock = getStockByNameOrId(stockNameOrId);
		if (stock == null) {
			return "新增失敗, 你輸入的商品: " + stockNameOrId + " 找不到";
		}
		buyStock.setId(userId + "-" + stock.getId());
		
		buyStock.setStockType(stock.getStockType());
		buyStock.setStockId(stock.getId());
		buyStock.setStockName(stock.getName()); 
		buyStock.setBuyPriceAndShares(buyPrice, buyShares);
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to save buy stock infomation, {}...", buyStock);
		treasuryStockRepo.save(buyStock);
		logger.info("<<<<< Save buy stock infomation done, time-spent: <{} ms>", (System.currentTimeMillis() - startTime));
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(buyDate);
		buffer.append(" 買進 (").append(stock.getId()).append(")").append(stock.getName());
		buffer.append(" $").append(buyPrice).append(" ");
		buffer.append(buyShares).append("股").append(", 資訊儲存成功");
		String resultMsg = buffer.toString();

		return resultMsg;
	}
	
	public String addSellStock(String userId, String sellDate, String stockNameOrId, double sellPrice, long sellShares) {
		TreasuryStock sellStock = new TreasuryStock();
		sellStock.setUserId(userId);
		try {
			sellStock.setSellDate(sellDate);
		} catch (ParseException e) {
			logger.error("Exception raised while paring buyDate, the correct format is 'yyyy/MM/dd'");
			return "新增失敗, 因日期格式錯誤";
		}

		Stock stock = getStockByNameOrId(stockNameOrId);
		if (stock == null) {
			return "新增失敗, 你輸入的商品: " + stockNameOrId + " 找不到";
		}
		sellStock.setId(userId + "-" + stock.getId());
		
		sellStock.setStockType(stock.getStockType());
		sellStock.setStockId(stock.getId());
		sellStock.setStockName(stock.getName()); 
		sellStock.setSellPriceAndShares(sellPrice, sellShares);
		
		long startTime = System.currentTimeMillis();
		logger.info(">>>>> Prepare to save sell stock infomation, {}...", sellStock);
		treasuryStockRepo.save(sellStock);
		logger.info("<<<<< Save sell stock infomation done, time-spent: <{} ms>", (System.currentTimeMillis() - startTime));
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(sellDate);
		buffer.append(" 賣出 (").append(stock.getId()).append(")").append(stock.getName());
		buffer.append(" $").append(sellPrice).append(" ");
		buffer.append(sellShares).append("股").append(", 資訊儲存成功");
		String resultMsg = buffer.toString();

		return resultMsg;
	}
	
	public String deleteTreasuryStock(String userId, String stockNameOrId) {
		Stock stock = getStockByNameOrId(stockNameOrId);
		if (stock == null) {
			return "刪除失敗, 你輸入的商品: " + stockNameOrId + " 找不到";
		}
		
		String key = userId + "-" + stock.getId();
		logger.info(">>>>> Prepare to delete treasury stock with key: {}...", key);
		treasuryStockRepo.delete(key);
		logger.info("<<<<< Delete treasury stock with key: {} done", key);
		
		return "刪除成功";
	}

	public String queryTreasuryStock(String userId) {
		List<TreasuryStock> treasuryStocks = treasuryStockRepo.findByUserId(userId);
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < treasuryStocks.size(); i++) {
			TreasuryStock treasuryStock = treasuryStocks.get(i);
			
			buffer.append("(").append(treasuryStock.getStockId()).append(") ").append(treasuryStock.getStockName()).append("\n");
			buffer.append("買進價: ").append(treasuryStock.getBuyPrice()).append("\n");
			buffer.append("買進股數: ").append(treasuryStock.getBuyShares()).append("\n");
			buffer.append("買進金額: ").append(treasuryStock.getBuyMatchAmount());
			
			double matchPrice = -1;
			try {
				matchPrice = getStockMatchPriceByNameOrId(treasuryStock.getId()).doubleValue();
			} catch (Exception e) {
				logger.error("Exception raised while tring to get match price with id: {}", treasuryStock.getId());
			}
			
			if (matchPrice != -1) {
				buffer.append("\n目前成交價: ").append(matchPrice).append("\n");

				BigDecimal currentAmount = new BigDecimal(matchPrice).multiply(new BigDecimal(treasuryStock.getBuyShares()));
				buffer.append("目前金額: ").append(currentAmount.doubleValue()).append("\n");
				
				BigDecimal fee = currentAmount.multiply(new BigDecimal(TreasuryStock.feePercent)).setScale(0, RoundingMode.FLOOR);
				buffer.append("賣出手續費: ").append(fee.doubleValue()).append("\n");
				
				BigDecimal sellTradeTax = currentAmount.multiply(new BigDecimal(TreasuryStock.sellTradeTaxPercent)).setScale(0, RoundingMode.FLOOR);
				buffer.append("賣出交易稅: ").append(sellTradeTax.doubleValue()).append("\n");
				
				BigDecimal currentSellMatchAmount = currentAmount.subtract(fee).subtract(sellTradeTax);
				buffer.append("賣出可得金額: ").append(currentSellMatchAmount.doubleValue()).append("\n");
				
				BigDecimal winLoseAmount = currentSellMatchAmount.subtract(new BigDecimal(treasuryStock.getBuyMatchAmount()));
				if (winLoseAmount.doubleValue() > 0) {
					winLoseAmount = winLoseAmount.setScale(0, RoundingMode.CEILING);
				}
				else {
					winLoseAmount = winLoseAmount.setScale(0, RoundingMode.FLOOR);
				}
				buffer.append("損益試算: ").append(winLoseAmount.doubleValue());
			}
			
			if (i != treasuryStocks.size() - 1) {
				buffer.append("\n-----------\n");
			}
		}
		
		return buffer.toString();
	}
}
