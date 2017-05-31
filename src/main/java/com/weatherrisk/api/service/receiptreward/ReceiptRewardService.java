package com.weatherrisk.api.service.receiptreward;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exfantasy.utils.tools.receipt_lottery.ReceiptLotteryNoUtil;
import com.exfantasy.utils.tools.receipt_lottery.Reward;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.model.receiptreward.ReceiptReward;
import com.weatherrisk.api.model.receiptreward.ReceiptRewardRepository;
import com.weatherrisk.api.util.BingoResult;
import com.weatherrisk.api.util.ReceiptRewardUtil;

@Service
public class ReceiptRewardService {
	
	private Logger logger = LoggerFactory.getLogger(ReceiptRewardService.class);
	
	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();
	
	@Autowired
	private ReceiptRewardRepository receiptRewardRepo;
	
	public void getNewestReceiptRewards() {
		List<Reward> rewards = ReceiptLotteryNoUtil.getReceiptLotteryNo();
		List<ReceiptReward> receiptRewards = new ArrayList<>();
		for (Reward reward : rewards) {
			receiptRewards.add(new ReceiptReward(reward.getSection(), reward.getRewardType(), reward.getNo()));
		}
		
		logger.info(">>>>> Prepare to delete all receipt rewards...");
		long startTime = System.currentTimeMillis();
		receiptRewardRepo.deleteAll();
		logger.info("<<<<< Delete all receipt rewards done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
		
		logger.info(">>>>> Prepare to save all receipt rewards, data-size: <{}>...", receiptRewards.size());
		startTime = System.currentTimeMillis();
		// insert is more faster than save
		receiptRewardRepo.insert(receiptRewards);
		logger.info("<<<<< Save all receipt rewards done, data-size: <{}>, time-spent: <{} ms>", receiptRewards.size(), System.currentTimeMillis() - startTime);
	}
	
	public String getRecentlyRewards() {
		waitForCreateDatasThreadComplete();
		
		List<ReceiptReward> rewards = receiptRewardRepo.findAll();

		StringBuilder buffer = new StringBuilder();
		if (rewards.isEmpty()) {
			buffer.append("查不到對應月份開獎資料");
		}
		else {
			Set<String> showedSection = new HashSet<>();
			
			for (int i = 0; i < rewards.size(); i++) {
				ReceiptReward reward = rewards.get(i);
				String section = reward.getSection();
				if (!showedSection.contains(section)) {
					showedSection.add(section);
					
					String[] yearMonths = section.split("_");
					String year = yearMonths[0];
					String months = yearMonths[1];
					if (i != 0) {
						buffer.append("\n");
					}
					buffer.append(year).append("年").append(months).append("月, 發票獎號如下:");
				}
				
				buffer.append("\n").append(reward.getRewardType().getKeyword()).append(": ").append(reward.getNo());
			}
		}
		return buffer.toString();
	}
	
	public String checkIsBingo(String lotteryNo) {
		waitForCreateDatasThreadComplete();
		
		List<ReceiptReward> receiptRewards = receiptRewardRepo.findAll();

		StringBuilder buffer = new StringBuilder();
		
		BingoResult bingo = ReceiptRewardUtil.checkIsBingo(lotteryNo, receiptRewards);
		
		switch (bingo.getBingoStatus()) {
			case NOT_GOT:
				buffer.append("你輸入的號碼未中獎");
				break;

			case GOT:
				DecimalFormat decimalFormat = new DecimalFormat("###,###");
				buffer.append("中獎金額: ").append(decimalFormat.format(bingo.getPrize())).append("\n");
				buffer.append("請參考").append(bingo.getSectionStr()).append(", ").append(bingo.getRewardType().getKeyword()).append(": ").append(bingo.getRewardNo());
				break;

			case MAYBE:
				buffer.append("可能中獎\n");
				buffer.append("請參考").append(bingo.getSectionStr()).append(", ").append(bingo.getRewardType().getKeyword()).append(": ").append(bingo.getRewardNo());
				break;
		}
		return buffer.toString();
	}
	
	private void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForReceiptReward().await();
		} catch (InterruptedException e) {}
	}
}
