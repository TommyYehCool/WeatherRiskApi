package com.weatherrisk.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.service.movie.AmbassadorMovieService;
import com.weatherrisk.api.service.movie.MiramarMovieService;
import com.weatherrisk.api.service.movie.ShowTimeMovieService;
import com.weatherrisk.api.service.movie.ViewshowMovieService;
import com.weatherrisk.api.service.movie.WovieMovieService;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_MovieService {

	@Autowired
	private ViewshowMovieService viewshowMovieService;
	
	@Autowired
	private ShowTimeMovieService showTimeMovieService;
	
	@Autowired
	private MiramarMovieService miramarMovieService;
	
	@Autowired
	private WovieMovieService wovieMovieService;
	
	@Autowired
	private AmbassadorMovieService ambassdorMovieSerice;
	
	@Test
	public void test_01_ViewshowMovieService_getXinyiMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_02_ViewshowMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("板橋大遠百威秀", "神鬼奇航");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_03_ShowTimeMovieService_getBanqiaoShowTimeMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_04_ShowTimeMoviceService_queryNowPlayingByTheaterName() {
		String queryResult = showTimeMovieService.queryNowPlayingByTheaterName("板橋秀泰");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_05_ShowTimeMoviceService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("板橋秀泰", "神鬼奇航");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_06_MiramarMovieService_getTachihMovieTimes() {
		System.out.println("Check the system init log");
	}

	@Test
	public void test_07_MiramarMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = miramarMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("大直美麗華", "神鬼奇航");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_08_WovieMovieService_getTenmouMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_09_WovieMovieService_queryNowPlayingByTheaterName() {
		String queryResult = wovieMovieService.queryNowPlayingByTheaterName("天母華威");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_10_WovieMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = wovieMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("天母華威", "神鬼奇航");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_11_AmbassadorMovieService_refresh() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_12_AmbassadorMovieService_queryNowPlayingByTheaterName() {
		String queryResult = ambassdorMovieSerice.queryNowPlayingByTheaterName("晶冠國賓");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_13_AmbassadorMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = ambassdorMovieSerice.queryMovieTimesByTheaterNameAndFilmNameLike("晶冠國賓", "異型");
		System.out.println(queryResult);
	}
}
