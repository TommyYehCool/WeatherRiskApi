package com.weatherrisk.api.service.movie;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherrisk.api.cnst.movie.theaters.ShowTimeTheater;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.config.movie.ShowTimeMovieConfig;
import com.weatherrisk.api.model.movie.MovieDateTime;
import com.weatherrisk.api.model.movie.ShowTimeMovie;
import com.weatherrisk.api.model.movie.ShowTimeMovieRepository;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.json.showtime.ShowTimeAllMoviesInfo;

@Service
public class ShowTimeMovieService implements MovieService {

	private Logger logger = LoggerFactory.getLogger(ShowTimeMovieService.class);
	
	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();
	
	@Autowired
	private ShowTimeMovieConfig showTimeMovieConfig;
	
	@Autowired
	private ShowTimeMovieRepository showTimeMovieRepo;
	
	@Override
	public void refreshMovieTimes() {
		try {
			deleteAllMovieTimes();
			getStarStarShowTimeMovieTimes();
			getTodayShowTimeMovieTimes();
			getBanqiaoShowTimeMovieTimes();
			getSoutheastShowTimeMovieTimes();
		}
		catch (Exception e) {
			logger.error("Exception raised while refresh Show Time movie times", e);
		}
	}

	@Override
	public void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all Show Time movie times...");
		long startTime = System.currentTimeMillis();
		showTimeMovieRepo.deleteAll();
		logger.info("<<<<< Delete all Show Time movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
	}
	
	private void getStarStarShowTimeMovieTimes() throws Exception {
		String url = showTimeMovieConfig.getStarStarShowTimeUrl();
		url += getTodayStr();
		String theaterName = ShowTimeTheater.STARSTAR.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private void getTodayShowTimeMovieTimes() throws Exception {
		String url = showTimeMovieConfig.getTodayShowTimeUrl();
		url += getTodayStr();
		String theaterName = ShowTimeTheater.TODAY.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private void getBanqiaoShowTimeMovieTimes() throws Exception {
		String url = showTimeMovieConfig.getBanqiaoShowTimeUrl();
		url += getTodayStr();
		String theaterName = ShowTimeTheater.BANQIAO.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private void getSoutheastShowTimeMovieTimes() throws Exception {
		String url = showTimeMovieConfig.getSoutheastShowTimeUrl();
		url += getTodayStr();
		String theaterName = ShowTimeTheater.SOUTHEAST.getChineseName();
		getShowTimeMovieTimes(url, theaterName);
	}
	
	private String getTodayStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return "?date=" + dateFormat.format(Calendar.getInstance().getTime());
	}
	
	private void getShowTimeMovieTimes(String url, String theaterName) throws Exception {
		logger.info(">>>>> Prepare to get Show Time movie times from url: <{}>", url);
		
		long startTime = System.currentTimeMillis();
		
		String jsonData = HttpUtil.sendGetByHttpClient(url);
		
		logger.info("<<<<< Get Show Time movie times from url: <{}> done, time-spent: <{} ms>", url, System.currentTimeMillis() - startTime);

		ObjectMapper mapper = new ObjectMapper();
		
		ShowTimeAllMoviesInfo showTimeAllMovieTimesInfo = mapper.readValue(jsonData, ShowTimeAllMoviesInfo.class);
		
		List<ShowTimeMovie> showTimeMoviesInfo = showTimeAllMovieTimesInfo.getShowTimeMovies();
		
		showTimeMoviesInfo.stream().forEach(showTimeMoive -> showTimeMoive.setTheaterName(theaterName));

		logger.info(">>>>> Prepare to insert all {} Show Time movie times, data-size: <{}>...", theaterName, showTimeMoviesInfo.size());
		startTime = System.currentTimeMillis();
		showTimeMovieRepo.insert(showTimeMoviesInfo);
		logger.info("<<<<< Insert all {} Show Time movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, showTimeMoviesInfo.size(), System.currentTimeMillis() - startTime);
	}

	@Override
	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Show Time movie time by theater: {}, filmName: {}", theaterName, filmName);
		List<ShowTimeMovie> showTimeMovies = showTimeMovieRepo.findByTheaterNameAndFilmNameLike(theaterName, filmName);
		if (!showTimeMovies.isEmpty()) {
			logger.info("<<<<< Query Show Time movie time by theaterName: {}, filmName: {} succeed, data-size: {}", theaterName, filmName, showTimeMovies.size());
			return constructQueryMovieTimesResult(showTimeMovies);
		}
		else {
			logger.info("<<<<< Query Show Time movie time by theaterName: {}, filmName: {} succeed, content is empty", theaterName, filmName);
			return "查不到對應電影資料";
		}
	}
	
	private String constructQueryMovieTimesResult(List<ShowTimeMovie> showTimeMovies) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < showTimeMovies.size(); i++) {
			ShowTimeMovie showTimeMovie = showTimeMovies.get(i);
			
			buffer.append(showTimeMovie.getFilmName()).append("\n");
			
			Map<String, LinkedList<String>> timesMap = new HashMap<>();
			
			for (int j = 0; j < showTimeMovie.getMovieDateTimes().size(); j++) {
				MovieDateTime movieDateTime = showTimeMovie.getMovieDateTimes().get(j);
				String date = movieDateTime.getDate();
				String time = movieDateTime.getSession();
				
				LinkedList<String> times;
				if (!timesMap.containsKey(date)) {
					times = new LinkedList<>();
					timesMap.put(date, times);
				}
				else {
					times = timesMap.get(date);
				}
				times.add(time);
			}
			
			String[] dates = timesMap.keySet().toArray(new String[0]);
			Arrays.sort(dates);
			
			for (String date : dates) {
				LinkedList<String> times = timesMap.get(date);

				Collections.sort(times);
				
				if (times.get(0).startsWith("00")) {
					String first = times.get(0);
					times.removeFirst();
					times.addLast(first);
				}
				
				buffer.append("\n日期: ").append(date);
				buffer.append("\n場次: ").append(StringUtils.join(times, ", "));
			}
			
			if (i != showTimeMovies.size() - 1) {
				buffer.append("=====================\n");
			}
		}
		return buffer.toString();
	}
	
	@Override
	public String queryNowPlayingByTheaterName(String theaterName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Show Time now playing by theater: {}", theaterName);
		List<ShowTimeMovie> showTimeMoives = showTimeMovieRepo.findByTheaterName(theaterName);
		if (!showTimeMoives.isEmpty()) {
			logger.info("<<<<< Query Show Time now playing by theaterName: {}, data-size: {}", theaterName, showTimeMoives.size());
			
			List<String> filmNames = showTimeMoives.stream().map(ShowTimeMovie::getFilmName).collect(Collectors.toList());
			
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(theaterName).append("上映電影如下:");
			for (String filmName : filmNames) {
				buffer.append("\n").append(filmName);
			}
			return buffer.toString();
		}
		else {
			logger.info("<<<<< Query Show Time now playing by theaterName: {}, content is empty", theaterName);
			return "查不到對應資料";
		}
	}
	
	@Override
	public void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForShowTimeMovie().await();
		} catch (InterruptedException e) {}
	}
}
