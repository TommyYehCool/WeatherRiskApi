package com.weatherrisk.api.service.receiptreward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exfantasy.utils.tools.receipt_lottery.Bingo;
import com.exfantasy.utils.tools.receipt_lottery.ReceiptLotteryNoUtil;
import com.exfantasy.utils.tools.receipt_lottery.Reward;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.model.receiptreward.ReceiptReward;
import com.weatherrisk.api.model.receiptreward.ReceiptRewardRepository;

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
			
			for (ReceiptReward reward : rewards) {
				String section = reward.getSection();
				if (!showedSection.contains(section)) {
					showedSection.add(section);
					
					String[] yearMonths = section.split("_");
					String year = yearMonths[0];
					String months = yearMonths[1];
					buffer.append(year).append("年").append(months).append("月, 發票獎號如下:\n");
				}
				
				buffer.append(reward.getRewardType().getKeyword()).append(": ").append(reward.getNo()).append("\n");
			}
		}
		return buffer.toString();
	}
	
	public String checkIsBingo(String lotteryNo) {
		List<ReceiptReward> receiptRewards = receiptRewardRepo.findAll();

		List<Reward> rewards = new ArrayList<>();
		for (ReceiptReward receiptReward : receiptRewards) {
			Reward reward = new Reward();
			reward.setSection(receiptReward.getSection());
			reward.setRewardType(receiptReward.getRewardType());
			reward.setNo(receiptReward.getNo());
		}
		
		StringBuilder buffer = new StringBuilder();
		
		Bingo bingo = ReceiptLotteryNoUtil.checkIsBingo(lotteryNo, rewards);
		if (!bingo.isBingo()) {
			buffer.append("你輸入的號碼未中獎");
		}
		else {
			buffer.append("請參考號碼: ").append(bingo.getLotteryNo()).append("\n");
			buffer.append("中獎金額: ").append(bingo.getPrize());
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
