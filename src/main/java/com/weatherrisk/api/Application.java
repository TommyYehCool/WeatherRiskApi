package com.weatherrisk.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.weatherrisk.api.service.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.ShowTimeMovieService;
import com.weatherrisk.api.service.TaipeiOpenDataService;
import com.weatherrisk.api.service.ViewshowMovieService;

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
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
	
	@PostConstruct
	public void postConstruct() {
		taipeiOpenDataService.getNewestParkingLotInfos();
		newTaipeiOpenDataService.getNewestParkingLotInfos();
		
		viewshowMovieService.refreshMovieTimes();
		
		showTimeMovieService.refreshMovieTimes();
	}
}
