package com.weatherrisk.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.MiramarTheater;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.config.MiramarMovieConfig;
import com.weatherrisk.api.model.MiramarMovie;
import com.weatherrisk.api.model.MiramarMovieRepository;
import com.weatherrisk.api.model.MovieDateTime;
import com.weatherrisk.api.util.HttpUtil;

@Service
public class MiramarMovieService {

	private Logger logger = LoggerFactory.getLogger(MiramarMovieService.class);

	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();

	@Autowired
	private MiramarMovieConfig miramarMovieConfig;
	
	@Autowired
	private MiramarMovieRepository miramarMovieRepo;
	
	public void refreshMovieTimes() {
		try {
			deleteAllMovieTimes();
			getTachihMovieTimes();
		} catch (Exception e) {
			logger.error("Exception raised while refresh Miramar movie times", e);
		}
	}

	private void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all Miramar movie times...");
		long startTime = System.currentTimeMillis();
		miramarMovieRepo.deleteAll();
		logger.info("<<<<< Delete all Miramar movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime); 
	}
	
	private void getTachihMovieTimes() throws Exception {
		String url = miramarMovieConfig.getTachihMiramarUrl();
		String theaterName = MiramarTheater.TACHIH.getChineseName();
		getMiramarMovieTimes(url, theaterName);
	}
	
	private void getMiramarMovieTimes(String url, String theaterName) throws Exception {
		Document document = HttpUtil.getDocument(url);
		
		// 儲存每場電影資訊
		List<MiramarMovie> miramarMovies = new ArrayList<>();
		
		Iterator<Element> itRowTimeContents = document.select("div#main > div.container.booking-style > div.row.time-content").iterator();
		while (itRowTimeContents.hasNext()) {
			MiramarMovie movie = new MiramarMovie();
			
			movie.setTheaterName(theaterName);
			
			Element itRowTimeContent = itRowTimeContents.next();
			
			String filmName = itRowTimeContent.select("div.span12 > div:has(h3) > h3").text();
			
			movie.setFilmName(filmName);
			
			// 整理日期及時間
			Map<String, LinkedList<String>> dateTimeMap = new TreeMap<>();
			
			Iterator<Element> elmDateTimes = itRowTimeContent.select("div.span9 > dl").iterator();
			while (elmDateTimes.hasNext()) {
				Element elmDateTime = elmDateTimes.next();
				
				String date = elmDateTime.select("dt").text().replaceAll(" ", "");
				date = date.substring(0, date.indexOf("日") + 1) + " " + date.substring(date.indexOf("日") + 1, date.length());
				
				Iterator<Element> itElmTimes = elmDateTime.select("a").iterator();
				
				while (itElmTimes.hasNext()) {
					String time = itElmTimes.next().text();
					
					LinkedList<String> times;
					if (!dateTimeMap.containsKey(date)) {
						times = new LinkedList<>();
						dateTimeMap.put(date, times);
					}
					else {
						times = dateTimeMap.get(date);
					}
					times.add(time);
				}
			}
			
			// 加入物件
			String[] dates = dateTimeMap.keySet().toArray(new String[0]);
			for (String date : dates) {
				String time = StringUtils.join(dateTimeMap.get(date), ", ");
				movie.addMovieDateTime(new MovieDateTime(date, time));
			}
			
			// 加入結果
			miramarMovies.add(movie);
		}
		
		logger.info(">>>>> Prepare to insert all {} Miramar movie times, data-size: <{}>...", theaterName, miramarMovies.size());
		long startTime = System.currentTimeMillis();
		miramarMovieRepo.insert(miramarMovies);
		logger.info("<<<<< Insert all {} Miramar movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, miramarMovies.size(), System.currentTimeMillis() - startTime);
	}
	
	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Miramar movie time by theater: {}, filmName: {}", theaterName, filmName);
		List<MiramarMovie> miramarMovies = miramarMovieRepo.findByTheaterNameAndFilmNameLike(theaterName, filmName);
		if (!miramarMovies.isEmpty()) {
			logger.info("<<<<< Query Miramar movie time by theaterName: {}, filmName: {} succeed, data-size: {}", theaterName, filmName, miramarMovies.size());
			return constructQueryMovieTimesResult(miramarMovies);
		}
		else {
			logger.info("<<<<< Query Miramar movie time by theaterName: {}, filmName: {} succeed, content is empty", theaterName, filmName);
			return "查不到對應電影資料";
		}
	}

	private String constructQueryMovieTimesResult(List<MiramarMovie> miramarMovies) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < miramarMovies.size(); i++) {
			MiramarMovie miramarMovie = miramarMovies.get(i);
			
			buffer.append(miramarMovie.getFilmName()).append("\n");
			buffer.append("\n");
			
			for (int j = 0; j < miramarMovie.getMovieDateTimes().size(); j++) {
				MovieDateTime movieDateTime = miramarMovie.getMovieDateTimes().get(j);
				buffer.append("日期: ").append(movieDateTime.getDate()).append("\n");
				buffer.append("場次: ").append(movieDateTime.getSession()).append("\n");
				if (j != miramarMovie.getMovieDateTimes().size() - 1) {
					buffer.append("----------------\n");
				}
			}
			if (i != miramarMovies.size() - 1) {
				buffer.append("=====================\n");
			}
		}
		return buffer.toString();
	}

	public String queryNowPlayingByTheaterName(String theaterName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Miramar now playing by theater: {}", theaterName);
		List<MiramarMovie> miramarMoives = miramarMovieRepo.findByTheaterName(theaterName);
		if (!miramarMoives.isEmpty()) {
			logger.info("<<<<< Query Miramar now playing by theaterName: {}, data-size: {}", theaterName, miramarMoives.size());
			
			List<String> filmNames = miramarMoives.stream().map(MiramarMovie::getFilmName).collect(Collectors.toList());
			
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(theaterName).append("上映電影如下:\n");
			for (String filmName : filmNames) {
				buffer.append(filmName).append("\n");
			}
			return buffer.toString();
		}
		else {
			logger.info("<<<<< Query Miramar now playing by theaterName: {}, content is empty", theaterName);
			return "查不到對應資料";
		}
	}
	
	private void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForMiramarMovie().await();
		} catch (InterruptedException e) {}
	}
}
