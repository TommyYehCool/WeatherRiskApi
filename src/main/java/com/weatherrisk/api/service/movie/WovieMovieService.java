package com.weatherrisk.api.service.movie;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.WovieTheater;
import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.config.movie.WovieMovieConfig;
import com.weatherrisk.api.model.movie.MovieDateTime;
import com.weatherrisk.api.model.movie.WovieMovie;
import com.weatherrisk.api.model.movie.WovieMovieRepository;
import com.weatherrisk.api.util.HttpUtil;

@Service
public class WovieMovieService {

	private Logger logger = LoggerFactory.getLogger(ViewshowMovieService.class);
	
	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();
	
	@Autowired
	private WovieMovieConfig wovieMovieConfig;
	
	@Autowired
	private WovieMovieRepository wovieMovieRepo;
	
	public void refreshMovieTimes() {
		try {
			deleteAllMovieTimes();
			getTienmouMovieTimes();
		} catch (Exception e) {
			logger.error("Exception raised while refresh Wovie movie times", e);
		}
	}

	private void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all Wovie movie times...");
		long startTime = System.currentTimeMillis();
		wovieMovieRepo.deleteAll();
		logger.info("<<<<< Delete all Wovie movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
	}

	private void getTienmouMovieTimes() throws Exception {
		String url = wovieMovieConfig.getTienmouWovieUrl();
		String theaterName = WovieTheater.TIENMOU.getChineseName();
		getWovieMovieTimes(url, theaterName);
	}

	private void getWovieMovieTimes(String url, String theaterName) throws Exception {
		Document document = HttpUtil.getDocument(url);
		
		// 儲存每場電影資訊
		List<WovieMovie> wovieMovies = new ArrayList<>();
		
		// 只能取得當天
		String todayString = getTodayStr();
		
		Iterator<Element> itFilmTables = document.select("table[align=\"center\"]").iterator();
		while (itFilmTables.hasNext()) {
			WovieMovie wovieMovie = new WovieMovie();
			
			wovieMovie.setTheaterName(theaterName);
			
			Element filmTable = itFilmTables.next();
			String filmName = filmTable.select("span.style4").first().text();
			
			wovieMovie.setFilmName(filmName);
			
			StringBuilder buffer = new StringBuilder();
			
			Iterator<Element> itTimesTables = filmTable.select("table[height=100%]").iterator();
			while (itTimesTables.hasNext()) {
				Element elmTimesTable = itTimesTables.next();

				Iterator<Element> itElmTimes = elmTimesTable.select("td.text-big1").iterator();
				while (itElmTimes.hasNext()) {
					String time = itElmTimes.next().text();
					time = time.substring(1, time.lastIndexOf("|") - 1);
					buffer.append(time).append(", ");
				}
			}
			
			String session = buffer.toString();
			session = session.substring(0, session.length() - 2);
			
			wovieMovie.addMovieDateTime(new MovieDateTime(todayString, session));
			
			wovieMovies.add(wovieMovie);
		}
		
		logger.info(">>>>> Prepare to insert all {} Wovie movie times, data-size: <{}>...", theaterName, wovieMovies.size());
		long startTime = System.currentTimeMillis();
		wovieMovieRepo.insert(wovieMovies);
		logger.info("<<<<< Insert all {} Wovie movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, wovieMovies.size(), System.currentTimeMillis() - startTime);
	}

	private String getTodayStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}

	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Wovie movie time by theater: {}, filmName: {}", theaterName, filmName);
		List<WovieMovie> wovieMovies = wovieMovieRepo.findByTheaterNameAndFilmNameLike(theaterName, filmName);
		if (!wovieMovies.isEmpty()) {
			logger.info("<<<<< Query Wovie movie time by theaterName: {}, filmName: {} succeed, data-size: {}", theaterName, filmName, wovieMovies.size());
			return constructQueryMovieTimesResult(wovieMovies);
		}
		else {
			logger.info("<<<<< Query Wovie movie time by theaterName: {}, filmName: {} succeed, content is empty", theaterName, filmName);
			return "查不到對應電影資料";
		}
	}
	
	private String constructQueryMovieTimesResult(List<WovieMovie> wovieMovies) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < wovieMovies.size(); i++) {
			WovieMovie wovieMovie = wovieMovies.get(i);
			
			buffer.append(wovieMovie.getFilmName()).append("\n");
			buffer.append("\n");
			
			for (int j = 0; j < wovieMovie.getMovieDateTimes().size(); j++) {
				MovieDateTime movieDateTime = wovieMovie.getMovieDateTimes().get(j);
				buffer.append("日期: ").append(movieDateTime.getDate()).append("\n");
				buffer.append("場次: ").append(movieDateTime.getSession()).append("\n");
				if (j != wovieMovie.getMovieDateTimes().size() - 1) {
					buffer.append("----------------\n");
				}
			}
			if (i != wovieMovies.size() - 1) {
				buffer.append("=====================\n");
			}
		}
		return buffer.toString();
	}
	
	public String queryNowPlayingByTheaterName(String theaterName) {
		waitForCreateDatasThreadComplete();
		
		logger.info(">>>>> Prepare to query Wovie now playing by theater: {}", theaterName);
		List<WovieMovie> wovieMoives = wovieMovieRepo.findByTheaterName(theaterName);
		if (!wovieMoives.isEmpty()) {
			logger.info("<<<<< Query Wovie now playing by theaterName: {}, data-size: {}", theaterName, wovieMoives.size());
			
			List<String> filmNames = wovieMoives.stream().map(WovieMovie::getFilmName).collect(Collectors.toList());
			
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(theaterName).append("上映電影如下:\n");
			for (String filmName : filmNames) {
				buffer.append(filmName).append("\n");
			}
			return buffer.toString();
		}
		else {
			logger.info("<<<<< Query Wovie now playing by theaterName: {}, content is empty", theaterName);
			return "查不到對應資料";
		}
	}

	private void waitForCreateDatasThreadComplete() {
		// 等待建立資料的 thread 處理完才進行查詢
		try {
			countDownHandler.getLatchForWovieMovie().await();
		} catch (InterruptedException e) {}
	}
}
