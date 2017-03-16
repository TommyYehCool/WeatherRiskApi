package com.weatherrisk.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.cnst.ViewshowTheater;
import com.weatherrisk.api.config.ViewshowMovieConfig;
import com.weatherrisk.api.model.MovieDateTime;
import com.weatherrisk.api.model.ViewShowMovie;
import com.weatherrisk.api.model.ViewShowMovieRepository;
import com.weatherrisk.api.util.HttpUtil;

@Service
public class ViewshowMovieService {
	
	private Logger logger = LoggerFactory.getLogger(ViewshowMovieService.class);

	@Autowired
	private ViewshowMovieConfig viewShowMovieConfig;
	
	@Autowired
	private ViewShowMovieRepository viewShowMovieRepo;
	
	public void refreshMovieTimes() {
		deleteAllMovieTimes();
		getXinyiMovieTimes();
		getQSquareMovieTimes();
		getSunMovieTimes();
		getMegaCityMovieTimes();
	}
	
	private void deleteAllMovieTimes() {
		logger.info(">>>>> Prepare to delete all movie times...");
		long startTime = System.currentTimeMillis();
		viewShowMovieRepo.deleteAll();
		logger.info("<<<<< Delete all movie times done, time-spent: <{} ms>", System.currentTimeMillis() - startTime);
	}
	
	private void getXinyiMovieTimes() {
		String url = viewShowMovieConfig.getXinyiViewshowUrl();
		String theaterName = ViewshowTheater.XINYI.getChineseName();
		getViewshowMovieTimes(url, theaterName);
	}
	
	private void getQSquareMovieTimes() {
		String url = viewShowMovieConfig.getQSquareViewshowUrl();
		String theaterName = ViewshowTheater.QSQUARE.getChineseName();
		getViewshowMovieTimes(url, theaterName);
	}
	
	private void getSunMovieTimes() {
		String url = viewShowMovieConfig.getSunViewshowUrl();
		String theaterName = ViewshowTheater.SUN.getChineseName();
		getViewshowMovieTimes(url, theaterName);
	}
	
	private void getMegaCityMovieTimes() {
		String url = viewShowMovieConfig.getMegaCityViewshowUrl();
		String theaterName = ViewshowTheater.MEGA_CITY.getChineseName();
		getViewshowMovieTimes(url, theaterName);
	}

	private void getViewshowMovieTimes(String url, String theaterName) {
		try {
			Document document = HttpUtil.getDocument(url);
			
			Element elmDataTable = document.select("div > table").first();
			
			Element elmDataTableFiratTBody = elmDataTable.select("tbody").first();
			
			Iterator<Element> itElmFirstTbodyTables = elmDataTableFiratTBody.select("table").iterator();
			
			// 儲存每場電影資訊
			List<ViewShowMovie> viewShowMovies = new ArrayList<>();
			
			boolean isSessionDuringTable = true;
			while (itElmFirstTbodyTables.hasNext()) {
				Element elmTable = itElmFirstTbodyTables.next();
				
				// 過濾掉場次時間表區間
				if (isSessionDuringTable) {
					isSessionDuringTable = false;
					continue;
				}
				else {
					ViewShowMovie movie = new ViewShowMovie();
					
					movie.setTheaterName(theaterName);
					
					Iterator<Element> itElmTrs = elmTable.select("tbody > tr").iterator();
					while (itElmTrs.hasNext()) {
						Element elmTr = itElmTrs.next();
						
						Element elmFilmName = elmTr.select("td.PrintShowTimesFilm").first();
						if (elmFilmName != null) {
							movie.setFilmName(elmFilmName.text());
							continue;
						}
						
						Element elmShowTimesDay = elmTr.select("td.PrintShowTimesDay").first();
						if (elmShowTimesDay != null) {
							Element elmShowTimesSession = elmTr.select("td.PrintShowTimesSession").first();
							
							MovieDateTime movieDateTime = new MovieDateTime();
							movieDateTime.setDate(elmShowTimesDay.text());
							movieDateTime.setSession(elmShowTimesSession.text());

							movie.addMovieDateTime(movieDateTime);
							continue;
						}
						
						if (elmFilmName == null && elmShowTimesDay == null) {
							viewShowMovies.add(movie);
							
							// 準備抓下一檔電影
							movie = new ViewShowMovie();
						}
					}
				}
			}
			
			long startTime = 0;
			
			logger.info(">>>>> Prepare to insert all {} movie times, data-size: <{}>...", theaterName, viewShowMovies.size());
			startTime = System.currentTimeMillis();
			viewShowMovieRepo.insert(viewShowMovies);
			logger.info("<<<<< Insert all {} movie times done, data-size: <{}>, time-spent: <{} ms>", theaterName, viewShowMovies.size(), System.currentTimeMillis() - startTime);
			
		} catch (Exception e) {
			logger.error("Exception raised while trying to get {} viewshow movie times", theaterName, e);
		}
	}

	public String queryMovieTimesByTheaterNameAndFilmNameLike(String theaterName, String filmName) {
		logger.info(">>>>> Prepare to query movie time by theater: {}, filmName: {}", theaterName, filmName);
		List<ViewShowMovie> viewShowMovies = viewShowMovieRepo.findByTheaterNameAndFilmNameLike(theaterName, filmName);
		if (!viewShowMovies.isEmpty()) {
			logger.info("<<<<< Query by theaterName: {}, filmName: {} succeed, content: {}", theaterName, filmName, viewShowMovies);
			return constructQueryMovieTimesResult(viewShowMovies);
		}
		else {
			logger.info("<<<<< Query by theaterName: {}, filmName: {} succeed, content is empty", theaterName, filmName, viewShowMovies);
			return "查不到對應電影資料";
		}
	}

	private String constructQueryMovieTimesResult(List<ViewShowMovie> viewShowMovies) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < viewShowMovies.size(); i++) {
			ViewShowMovie viewShowMovie = viewShowMovies.get(i);
			
			buffer.append(viewShowMovie.getFilmName()).append("\n");
			buffer.append("\n");
			
			for (int j = 0; j < viewShowMovie.getMovieDateTimes().size(); j++) {
				MovieDateTime movieDateTime = viewShowMovie.getMovieDateTimes().get(j);
				buffer.append("日期: ").append(movieDateTime.getDate()).append("\n");
				buffer.append("場次: ").append(movieDateTime.getSession()).append("\n");
				if (j != viewShowMovie.getMovieDateTimes().size() - 1) {
					buffer.append("----------------\n");
				}
			}
			if (i != viewShowMovies.size() - 1) {
				buffer.append("=====================\n");
			}
		}
		return buffer.toString();
	}

	public String queryNowPlayingByTheaterName(String theaterName) {
		logger.info(">>>>> Prepare to query now playing by theater: {}", theaterName);
		List<ViewShowMovie> viewShowMoives = viewShowMovieRepo.findByTheaterName(theaterName);
		if (!viewShowMoives.isEmpty()) {
			logger.info("<<<<< Query by theaterName: {}, content: {}", viewShowMoives);
			
			List<String> filmNames = viewShowMoives.stream().map(ViewShowMovie::getFilmName).collect(Collectors.toList());
			
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(theaterName).append("上映電影如下:\n");
			for (String filmName : filmNames) {
				buffer.append(filmName).append("\n");
			}
			return buffer.toString();
		}
		else {
			logger.info("<<<<< Query by theaterName: {}, content is empty", theaterName);
			return "查不到對應資料";
		}
	}
}
