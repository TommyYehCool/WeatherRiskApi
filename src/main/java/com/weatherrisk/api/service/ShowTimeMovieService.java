package com.weatherrisk.api.service;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.ShowTimeTheater;
import com.weatherrisk.api.config.ShowTimeMovieConfig;
import com.weatherrisk.api.util.HttpUtil;

@Service
public class ShowTimeMovieService {

	private Logger logger = LoggerFactory.getLogger(ShowTimeMovieService.class);
	
	@Autowired
	private ShowTimeMovieConfig showTimeMovieConfig;
	
	public void refreshMovieTimes() {
		deleteAllMovieTimes();
		getBanqiaoShowTimeMovieTimes();
	}

	private void deleteAllMovieTimes() {
		// TODO Auto-generated method stub
	}
	
	private void getBanqiaoShowTimeMovieTimes() {
		String url = showTimeMovieConfig.getBanqiaoShowTimeUrl();
		String theaterName = ShowTimeTheater.BANQIAO.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private void getShowTimeMovieTimes(String url, String theaterName) {
		try {
			Document document = HttpUtil.getDocument(url);
			
			// System.out.println(document);

			// FIXME 不知道怎麼取得網站資訊, 先擺著
			
			// logger.info(">>>>> Prepare to insert all {} movie times, data-size: <{}>...", theaterName, viewShowMovies.size());
			long startTime = System.currentTimeMillis();
			// TODO save to mongodb
			// logger.info("<<<<< Insert all {} movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, viewShowMovies.size(), System.currentTimeMillis() - startTime);
			
		} catch (Exception e) {
			logger.error("Exception raised while trying to get {} show time movie times", theaterName, e);
		}
	}
}
