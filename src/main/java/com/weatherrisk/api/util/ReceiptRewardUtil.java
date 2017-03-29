package com.weatherrisk.api.util;

import java.util.List;

import com.exfantasy.utils.tools.receipt_lottery.RewardType;
import com.weatherrisk.api.model.receiptreward.ReceiptReward;
import com.weatherrisk.api.util.BingoResult.BingoStatus;

public class ReceiptRewardUtil {

	public static BingoResult checkIsBingo(String lotteryNo, List<ReceiptReward> receiptRewards) {
		BingoResult bingo = new BingoResult();
		for (ReceiptReward receiptReward : receiptRewards) {
			bingo.setSection(receiptReward.getSection());

			String rewardNo = receiptReward.getNo();
			bingo.setRewardNo(rewardNo);
			
			RewardType rewardType = receiptReward.getRewardType();
			bingo.setRewardType(rewardType);
			switch (rewardType) {
				// 特別獎
				case FIRST_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(rewardNo)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(10000000L);
						return bingo;
					}
					else if (rewardNo.endsWith(lotteryNo)) {
						bingo.setBingoStatus(BingoStatus.MAYBE);
						return bingo;
					}
					break;

				// 特獎
				case SEONCD_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(rewardNo)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(2000000L);
						return bingo;
					}
					else if (rewardNo.endsWith(lotteryNo)) {
						bingo.setBingoStatus(BingoStatus.MAYBE);
						return bingo;
					}
					break;

				// 頭獎
				case THIRD_REWARD:
					// 號碼完全相同
					if (lotteryNo.equals(rewardNo)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(200000L);
						return bingo;
					}
					else if (rewardNo.endsWith(lotteryNo)) {
						int length = lotteryNo.length();
						switch (length) {
							case 7:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(40000L);
								return bingo;
							case 6:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(10000L);
								return bingo;
							case 5:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(4000L);
								return bingo;
							case 4:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(1000L);
								return bingo;
							case 3:
								bingo.setBingoStatus(BingoStatus.GOT);
								bingo.setPrize(200L);
								return bingo;
							default:
								bingo.setBingoStatus(BingoStatus.NOT_GOT);
								return bingo;
						}
					}
					break;

				// 增開六獎
				case SPECIAL_SIX:
					String last3OfLotteryNo = lotteryNo.substring(lotteryNo.length() - 3, lotteryNo.length());
					if (last3OfLotteryNo.equals(rewardNo)) {
						bingo.setBingoStatus(BingoStatus.GOT);
						bingo.setPrize(200L);
						return bingo;
					}
					break;
			}
		}
		return bingo;
	}
}
