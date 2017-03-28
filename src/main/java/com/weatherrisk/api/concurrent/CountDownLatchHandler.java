package com.weatherrisk.api.concurrent;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchHandler {
	private static CountDownLatchHandler instance = new CountDownLatchHandler();
	
	private CountDownLatch latchForParkingLot;
	
	private CountDownLatch latchForViewShowMovie;
	
	private CountDownLatch latchForShowTimeMovie;
	
	private CountDownLatch latchForMiramarMovie;
	
	private CountDownLatch latchForWovieMovie;
	
	private CountDownLatch latchForAmbassadorMovie;
	
	private CountDownLatch latchForReceiptReward;
	
	public static CountDownLatchHandler getInstance() {
		return instance;
	}
	
	public void setLatchForParkingLot(int threadCount) {
		latchForParkingLot = new CountDownLatch(threadCount);
	}
	
	public void setLatchForViewShowMovie(int threadCount) {
		latchForViewShowMovie = new CountDownLatch(threadCount);
	}
	
	public void setLatchForShowTimeMovie(int threadCount) {
		latchForShowTimeMovie = new CountDownLatch(threadCount);
	}
	
	public void setLatchForMiramarMovie(int threadCount) {
		latchForMiramarMovie = new CountDownLatch(threadCount);
	}
	
	public void setLatchForWovieMovie(int threadCount) {
		latchForWovieMovie = new CountDownLatch(threadCount);
	}
	
	public void setLatchForAmbassadorMovie(int threadCount) {
		latchForAmbassadorMovie = new CountDownLatch(threadCount);
	}
	
	public void setLatchForReceiptReward(int threadCount) {
		latchForReceiptReward = new CountDownLatch(threadCount);
	}

	public CountDownLatch getLatchForParkingLot() {
		return latchForParkingLot;
	}

	public CountDownLatch getLatchForViewShowMovie() {
		return latchForViewShowMovie;
	}

	public CountDownLatch getLatchForShowTimeMovie() {
		return latchForShowTimeMovie;
	}

	public CountDownLatch getLatchForMiramarMovie() {
		return latchForMiramarMovie;
	}

	public CountDownLatch getLatchForWovieMovie() {
		return latchForWovieMovie;
	}
	
	public CountDownLatch getLatchForAmbassadorMovie() {
		return latchForAmbassadorMovie;
	}
	
	public CountDownLatch getLatchForReceiptReward() {
		return latchForReceiptReward;
	}
}
