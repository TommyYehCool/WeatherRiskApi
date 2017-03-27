package com.weatherrisk.test;

import java.io.IOException;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.weatherrisk.api.Application;
import com.weatherrisk.api.cnst.CurrencyCnst;
import com.weatherrisk.api.service.currency.CurrencyService;
import com.weatherrisk.api.service.movie.AmbassadorMovieService;
import com.weatherrisk.api.service.movie.MiramarMovieService;
import com.weatherrisk.api.service.movie.ShowTimeMovieService;
import com.weatherrisk.api.service.movie.ViewshowMovieService;
import com.weatherrisk.api.service.movie.WovieMovieService;
import com.weatherrisk.api.service.opendata.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.opendata.TaipeiOpenDataService;
import com.weatherrisk.api.service.parking.ParkingLotService;
import com.weatherrisk.api.service.weather.CwbService;
import com.weatherrisk.api.vo.json.tpeopendata.ubike.UBikeInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = Application.class,
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_GetDataService {
	
	@Autowired
	private CwbService cwbService;
	
	@Autowired
	private CurrencyService bitcoinService;
	
	@Autowired
	private TaipeiOpenDataService taipeiOpenDataService;
	
	@Autowired
	private NewTaipeiOpenDataService newTaipeiOpenDataService;
	
	@Autowired
	private ParkingLotService parkingLotService;
	
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
	public void test_1_CwbService_getWeatherLittleHelperByCity() {
		String data = cwbService.getWeatherLitteleHelperByCity("台北市");
		System.out.println(data);
	}
	
	@Test
	public void test_2_CwbService_getOneWeekWeatherPrediction() {
		String data = cwbService.getOneWeekWeatherPrediction("臺北市");
		System.out.println(data);
		
		data = cwbService.getOneWeekWeatherPrediction("桃園市");
		System.out.println(data);
	}
	
	@Test
	public void test_3_BitcoinService_getBitcoinPriceFromWinkdex() {
		String data = bitcoinService.getBitcoinPriceFromWinkdex();
		System.out.println(data);
	}
	
	@Test
	public void test_4_BitcoinService_getPriceFromExchanges() {
		String data = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.BTC_USD);
		System.out.println(data);
		
		data = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.ETH_USD);
		System.out.println(data);
	}
	
	@Test
	public void test_5_BitcoinService_getRatesFromTaiwanBank() throws IOException {
		String data = bitcoinService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.USD);
		System.out.println(data);
		
		data = bitcoinService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.JPY);
		System.out.println(data);
	}
	
	@Test
	public void test_6_TaipeiOpenDataService_getNewestUBikeInfo() {
		String data = taipeiOpenDataService.getNewestUBikeInfoByNameLike("士林");
		System.out.println(data);
	}
	
	@Test
	public void test_7_NewTaipeiOpenDataService_getNewestUBikeInfo() {
		String data = newTaipeiOpenDataService.getNewestUBikeInfoByNameLike("三重");
		System.out.println(data);
	}
	
	@Test
	public void test_8_TaipeiOpenDataService_getNearbyUBikeStations() {
		double userLatitude = 25.041861;
		double userLongitude = 121.554212;
		List<UBikeInfo> nearbyUBikeStations = taipeiOpenDataService.getNearbyUBikeStations(userLatitude, userLongitude);
		nearbyUBikeStations.stream().forEach(System.out::println);
	}
	
	@Test
	public void test_9_ParkingLotService_findByName() {
		String result = parkingLotService.findByNameLike("洛陽");
		System.out.println(result);
	}
	
	@Test
	public void test_10_NewTaipeiOpenDataService_getNewestParkingLotAvailable() {
		newTaipeiOpenDataService.getNewestParkingLotAvailable();
	}
	
	@Test
	public void test_11_ViewshowMovieService_getXinyiMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_12_ViewshowMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("京站威秀", "金剛");
		System.out.println(queryResult);

		queryResult = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("板橋大遠百威秀", "金剛");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_13_ShowTimeMovieService_getBanqiaoShowTimeMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_14_ShowTimeMoviceService_queryNowPlayingByTheaterName() {
		String queryResult = showTimeMovieService.queryNowPlayingByTheaterName("板橋秀泰");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_15_ShowTimeMoviceService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("板橋秀泰", "她其實");
		System.out.println(queryResult);
		
		queryResult = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("欣欣秀泰", "她其實");
		System.out.println(queryResult);
		
		queryResult = showTimeMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("今日秀泰", "攻敵");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_16_MiramarMovieService_getTachihMovieTimes() {
		System.out.println("Check the system init log");
	}

	@Test
	public void test_17_MiramarMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = miramarMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("大直美麗華", "她其實");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_18_WovieMovieService_getTenmouMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_19_WovieMovieService_queryNowPlayingByTheaterName() {
		String queryResult = wovieMovieService.queryNowPlayingByTheaterName("天母華威");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_20_WovieMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = wovieMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("天母華威", "羅根");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_21_AmbassadorMovieService_refresh() {
		System.out.println("Check the system init log");
	}
	
	@Test
	public void test_22_AmbassadorMovieService_queryNowPlayingByTheaterName() {
		String queryResult = ambassdorMovieSerice.queryNowPlayingByTheaterName("晶冠國賓");
		System.out.println(queryResult);
	}
	
	@Test
	public void test_23_AmbassadorMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = ambassdorMovieSerice.queryMovieTimesByTheaterNameAndFilmNameLike("晶冠國賓", "羅根");
		System.out.println(queryResult);
	}
}
