package com.weatherrisk.api.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.ShowTimeTheater;
import com.weatherrisk.api.config.ShowTimeMovieConfig;
import com.weatherrisk.api.model.ShowTimeMovie;
import com.weatherrisk.api.model.ShowTimeMovieRepository;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.showtime.ShowTimeAllMoviesInfo;

@Service
public class ShowTimeMovieService {

	private Logger logger = LoggerFactory.getLogger(ShowTimeMovieService.class);
	
	@Autowired
	private ShowTimeMovieConfig showTimeMovieConfig;
	
	@Autowired
	private ShowTimeMovieRepository showTimeMovieRepo;
	
	public void refreshMovieTimes() {
		try {
			deleteAllMovieTimes();
			getBanqiaoShowTimeMovieTimes();
		}
		catch (Exception e) {
			logger.error("Exception raised while refresh show time movie times", e);
		}
	}

	private void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all show time movie times...");
		long startTime = System.currentTimeMillis();
		showTimeMovieRepo.deleteAll();
		logger.info("<<<<< Delete all show time movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
	}
	
	private void getBanqiaoShowTimeMovieTimes() throws Exception {
		String url = showTimeMovieConfig.getMovieInfo_BanqiaoShowTimeUrl();
		url += getTodayStr();
		String theaterName = ShowTimeTheater.BANQIAO.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private String getTodayStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return "?date=" + dateFormat.format(Calendar.getInstance().getTime());
	}
	
	private void getShowTimeMovieTimes(String url, String theaterName) throws Exception {
		logger.info(">>>>> Prepare to get show time movie times from url: <{}>", url);
		
		long startTime = System.currentTimeMillis();
		
		String jsonData = HttpUtil.getJsonContentFromOpenData(url);
		
		logger.info("<<<<< Get show time movie times from url: <{}> done, time-spent: <{} ms>", url, System.currentTimeMillis() - startTime);

		ObjectMapper mapper = new ObjectMapper();
		
		ShowTimeAllMoviesInfo showTimeAllMovieTimesInfo = mapper.readValue(jsonData, ShowTimeAllMoviesInfo.class);
		
		List<ShowTimeMovie> showTimeMoviesInfo = showTimeAllMovieTimesInfo.getShowTimeMovies();
		
		showTimeMoviesInfo.stream().forEach(showTimeMoive -> showTimeMoive.setTheaterName(theaterName));

		logger.info(">>>>> Prepare to insert all {} movie times, data-size: <{}>...", theaterName, showTimeMoviesInfo.size());
		startTime = System.currentTimeMillis();
		showTimeMovieRepo.insert(showTimeMoviesInfo);
		logger.info("<<<<< Insert all {} movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, showTimeMoviesInfo.size(), System.currentTimeMillis() - startTime);
	}
}
