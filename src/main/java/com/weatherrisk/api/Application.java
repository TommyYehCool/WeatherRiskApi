package com.weatherrisk.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.weatherrisk.api.concurrent.CountDownLatchHandler;
import com.weatherrisk.api.service.MiramarMovieService;
import com.weatherrisk.api.service.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.ShowTimeMovieService;
import com.weatherrisk.api.service.TaipeiOpenDataService;
import com.weatherrisk.api.service.ViewshowMovieService;
import com.weatherrisk.api.service.WovieMovieService;

/**
 * <pre>
 * 程式啟動點 
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;

	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;
	
	@Autowired
	private ViewshowMovieService viewshowMovieService;
	
	@Autowired
	private ShowTimeMovieService showTimeMovieService;
	
	@Autowired
	private MiramarMovieService miramarMovieService;
	
	@Autowired
	private WovieMovieService wovieMovieService;
	
	private CountDownLatchHandler countDownHandler = CountDownLatchHandler.getInstance();
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
	
	@PostConstruct
	public void postConstruct() {
		countDownHandler.setLatchForParkingLot(1);
		new Thread(() -> {
			taipeiOpenDataService.getNewestParkingLotInfos();
			newTaipeiOpenDataService.getNewestParkingLotInfos();
			
			countDownHandler.getLatchForParkingLot().countDown();
		}).start();

		countDownHandler.setLatchForViewShowMovie(1);
		new Thread(() -> {
			viewshowMovieService.refreshMovieTimes();
			
			countDownHandler.getLatchForViewShowMovie().countDown();
		}).start();
		
		countDownHandler.setLatchForShowTimeMovie(1);
		new Thread(() -> {
			showTimeMovieService.refreshMovieTimes();
			
			countDownHandler.getLatchForShowTimeMovie().countDown();
		}).start();
		
		countDownHandler.setLatchForMiramarMovie(1);
		new Thread(() -> {
			miramarMovieService.refreshMovieTimes();
			
			countDownHandler.getLatchForMiramarMovie().countDown();
		}).start();
		
		countDownHandler.setLatchForWovieMovie(1);
		new Thread(() -> {
			wovieMovieService.refreshMovieTimes();
			
			countDownHandler.getLatchForWovieMovie().countDown();
		}).start();
	}
}
