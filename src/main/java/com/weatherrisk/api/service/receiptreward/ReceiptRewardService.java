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
import com.exfantasy.utils.tools.receipt_lottery.RewardType;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.model.receiptreward.ReceiptReward;
import com.weatherrisk.api.model.receiptreward.ReceiptRewardRepository;

import lombok.Data;

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
		waitForCreateDatasThreadComplete();
		
		List<ReceiptReward> receiptRewards = receiptRewardRepo.findAll();

		StringBuilder buffer = new StringBuilder();
		
		BingoResult bingo = checkIsBingo(lotteryNo, receiptRewards);
		
		switch (bingo.getBingoStatus()) {
			case NOT_GOT:
				buffer.append("你輸入的號碼未中獎");
				break;

			case GOT:
				DecimalFormat decimalFormat = new DecimalFormat("###,###");
				buffer.append("請參考號碼: ").append(bingo.getRewardNo()).append("\n");
				buffer.append("中獎金額: ").append(decimalFormat.format(bingo.getPrize()));
				break;

			case MAYBE:
				buffer.append("可能中獎, 請參考號碼: ").append(bingo.getRewardNo());
				break;
		}
		return buffer.toString();
	}
	
	private BingoResult checkIsBingo(String lotteryNo, List<ReceiptReward> receiptRewards) {
		BingoResult bingo = new BingoResult();
		for (ReceiptReward receiptReward : receiptRewards) {
			RewardType rewardType = receiptReward.getRewardType();
			String number = receiptReward.getNo();
			
			switch (rewardType) {
				// 特別獎
				case FIRST_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(number)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(10000000L);
						return bingo;
					}
					else if (number.endsWith(lotteryNo)) {
						bingo.setBingoStatus(BingoStatus.MAYBE);
						return bingo;
					}
					break;

				// 特獎
				case SEONCD_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(number)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(2000000L);
						return bingo;
					}
					else if (number.endsWith(lotteryNo)) {
						bingo.setBingoStatus(BingoStatus.MAYBE);
						return bingo;
					}
					break;

				// 頭獎
				case THIRD_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(number)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(200000L);
						return bingo;
					}
					else if (number.endsWith(lotteryNo)) {
						int length = lotteryNo.length();
						switch (length) {
							case 7:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(40000L);
								break;
							case 6:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(10000L);
								break;
							case 5:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(4000L);
								break;
							case 4:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(1000L);
								break;
							case 3:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(200L);
								break;
							default:
								bingo.setBingoStatus(BingoStatus.NOT_GOT);
								break;
						}
					}
					break;

				// 增開六獎
				case SPECIAL_SIX:
					String last3OfLotteryNo = lotteryNo.substring(lotteryNo.length() - 3, lotteryNo.length());
					if (last3OfLotteryNo.equals(number)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(200L);
						return bingo;
					}
					break;
			}
		}
		return bingo;
	}
	
	@Data
	private class BingoResult {
		private String rewardNo;
		private BingoStatus bingoStatus = BingoStatus.NOT_GOT;
		private Long prize;
	}
	
	private enum BingoStatus { 
		NOT_GOT, GOT, MAYBE;
	}
	
	private void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForReceiptReward().await();
		} catch (InterruptedException e) {}
	}
}
