package com.weatherrisk.api.service.movie;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.AmbassadorTheater;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.config.movie.AmbassadorMovieConfig;
import com.weatherrisk.api.model.movie.AmbassadorMovie;
import com.weatherrisk.api.model.movie.AmbassadorMovieRepository;
import com.weatherrisk.api.model.movie.MovieDateTime;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.ambassador.AmbassadorAllMoviesInfo;

@Service
public class AmbassadorMovieService {

	private Logger logger = LoggerFactory.getLogger(ShowTimeMovieService.class);
	
	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();
	
	@Autowired
	private AmbassadorMovieConfig ambassadorMovieConfig;
	
	@Autowired
	private AmbassadorMovieRepository ambassadorMovieRepo;
	
	public void refreshMovieTimes() {
		try {
			deleteAllMovieTimes();
			getXimenAmbassadorMovieTimes();
			getBreezeAmbassadorMovieTimes();
			getCrownAmbassadorMovieTimes();
		} catch (Exception e) {
			logger.error("Exception raised while refresh Ambassador movie times", e);
		}
	}

	private void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all Ambassador movie times...");
		long startTime = System.currentTimeMillis();
		ambassadorMovieRepo.deleteAll();
		logger.info("<<<<< Delete all Ambassador movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
	}
	
	private void getXimenAmbassadorMovieTimes() throws Exception {
		String url = ambassadorMovieConfig.getXimenAmbassadorUrl();
		url += getTodayStr();
		String theaterName = AmbassadorTheater.XIMEN.getChineseName();
		getAmbassadorMovieTime(url, theaterName);
	}

	private void getBreezeAmbassadorMovieTimes() throws Exception {
		String url = ambassadorMovieConfig.getBreezeAmbassdorUrl();
		url += getTodayStr();
		String theaterName = AmbassadorTheater.BREEZE.getChineseName();
		getAmbassadorMovieTime(url, theaterName);
	}

	private void getCrownAmbassadorMovieTimes() throws Exception {
		String url = ambassadorMovieConfig.getCrownAmbassadorUrl();
		url += getTodayStr();
		String theaterName = AmbassadorTheater.CROWN.getChineseName();
		getAmbassadorMovieTime(url, theaterName);
	}
	
	private String getTodayStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		return "&showingDate=" + dateFormat.format(Calendar.getInstance().getTime());
	}

	private void getAmbassadorMovieTime(String url, String theaterName) throws Exception {
		logger.info(">>>>> Prepare to get Ambassador movie times from url: <{}>", url);
		
		long startTime = System.currentTimeMillis();
		
		String jsonData = HttpUtil.sendGetByHttpClient(url);
		
		logger.info("<<<<< Get Ambassador movie times from url: <{}> done, time-spent: <{} ms>", url, System.currentTimeMillis() - startTime);

		ObjectMapper mapper = new ObjectMapper();
		
		AmbassadorAllMoviesInfo ambassadorAllMovieTimesInfo = mapper.readValue(jsonData, AmbassadorAllMoviesInfo.class);
		
		List<AmbassadorMovie> ambassadorMoviesInfo = ambassadorAllMovieTimesInfo.getAmbassadorMovies();
		
		ambassadorMoviesInfo.stream().forEach(ambassadorMovie -> ambassadorMovie.setTheaterName(theaterName));

		logger.info(">>>>> Prepare to insert all {} Ambassador movie times, data-size: <{}>...", theaterName, ambassadorMoviesInfo.size());
		startTime = System.currentTimeMillis();
		ambassadorMovieRepo.insert(ambassadorMoviesInfo);
		logger.info("<<<<< Insert all {} Ambassador movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, ambassadorMoviesInfo.size(), System.currentTimeMillis() - startTime);
	}
	
	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Ambassador movie time by theater: {}, filmName: {}", theaterName, filmName);
		List<AmbassadorMovie> ambassadorMovies = ambassadorMovieRepo.findByTheaterNameAndFilmNameLike(theaterName, filmName);
		if (!ambassadorMovies.isEmpty()) {
			logger.info("<<<<< Query Ambassador movie time by theaterName: {}, filmName: {} succeed, data-size: {}", theaterName, filmName, ambassadorMovies.size());
			return constructQueryMovieTimesResult(ambassadorMovies);
		}
		else {
			logger.info("<<<<< Query Ambassador movie time by theaterName: {}, filmName: {} succeed, content is empty", theaterName, filmName);
			return "查不到對應電影資料";
		}
	}

	private String constructQueryMovieTimesResult(List<AmbassadorMovie> ambassadorMovies) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < ambassadorMovies.size(); i++) {
			AmbassadorMovie ambassadorMovie = ambassadorMovies.get(i);
			
			buffer.append(ambassadorMovie.getFilmName()).append("\n");
			buffer.append("\n");
			
			for (int j = 0; j < ambassadorMovie.getMovieDateTimes().size(); j++) {
				MovieDateTime movieDateTime = ambassadorMovie.getMovieDateTimes().get(j);
				String date = movieDateTime.getDate();
				String time = movieDateTime.getSession();
				
				buffer.append("日期: ").append(date).append("\n");
				buffer.append("場次: ").append(time).append("\n");
			}
			
			if (i != ambassadorMovies.size() - 1) {
				buffer.append("=====================\n");
			}
		}
		return buffer.toString();
	}
	
	public String queryNowPlayingByTheaterName(String theaterName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Ambassador now playing by theater: {}", theaterName);
		List<AmbassadorMovie> ambassadorMovies = ambassadorMovieRepo.findByTheaterName(theaterName);
		if (!ambassadorMovies.isEmpty()) {
			logger.info("<<<<< Query Ambassador now playing by theaterName: {}, data-size: {}", theaterName, ambassadorMovies.size());
			
			List<String> filmNames = ambassadorMovies.stream().map(AmbassadorMovie::getFilmName).collect(Collectors.toList());
			
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(theaterName).append("上映電影如下:\n");
			for (String filmName : filmNames) {
				buffer.append(filmName).append("\n");
			}
			return buffer.toString();
		}
		else {
			logger.info("<<<<< Query Show Time now playing by theaterName: {}, content is empty", theaterName);
			return "查不到對應資料";
		}
	}

	private void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForAmbassadorMovie().await();
		} catch (InterruptedException e) {}
	}
}
