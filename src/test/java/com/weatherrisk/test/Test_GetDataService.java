package com.weatherrisk.test;

import java.io.IOException;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
import com.weatherrisk.api.service.CurrencyService;
import com.weatherrisk.api.service.CwbService;
import com.weatherrisk.api.service.NewTaipeiOpenDataService;
import com.weatherrisk.api.service.ParkingLotService;
import com.weatherrisk.api.service.ShowTimeMovieService;
import com.weatherrisk.api.service.TaipeiOpenDataService;
import com.weatherrisk.api.service.ViewshowMovieService;
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
	
	@Test
	@Ignore
	public void test_1_CwbService_getWeatherLittleHelperByCity() {
		String data = cwbService.getWeatherLitteleHelperByCity("台北市");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_2_CwbService_getOneWeekWeatherPrediction() {
		String data = cwbService.getOneWeekWeatherPrediction("臺北市");
		System.out.println(data);
		
		data = cwbService.getOneWeekWeatherPrediction("桃園市");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_3_BitcoinService_getBitcoinPriceFromWinkdex() {
		String data = bitcoinService.getBitcoinPriceFromWinkdex();
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_4_BitcoinService_getPriceFromExchanges() {
		String data = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.BTC_USD);
		System.out.println(data);
		
		data = bitcoinService.getCryptoCurrencyPriceFromExchanges(CurrencyPair.ETH_USD);
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_5_BitcoinService_getRatesFromTaiwanBank() throws IOException {
		String data = bitcoinService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.USD);
		System.out.println(data);
		
		data = bitcoinService.getRealCurrencyRatesFromTaiwanBank(CurrencyCnst.JPY);
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_6_TaipeiOpenDataService_getNewestUBikeInfo() {
		String data = taipeiOpenDataService.getNewestUBikeInfoByNameLike("士林");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_7_NewTaipeiOpenDataService_getNewestUBikeInfo() {
		String data = newTaipeiOpenDataService.getNewestUBikeInfoByNameLike("三重");
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void test_8_TaipeiOpenDataService_getNearbyUBikeStations() {
		double userLatitude = 25.041861;
		double userLongitude = 121.554212;
		List<UBikeInfo> nearbyUBikeStations = taipeiOpenDataService.getNearbyUBikeStations(userLatitude, userLongitude);
		nearbyUBikeStations.stream().forEach(System.out::println);
	}
	
	@Test
	@Ignore
	public void test_9_ParkingLotService_findByName() {
		String result = parkingLotService.findByNameLike("洛陽");
		System.out.println(result);
	}
	
	@Test
	@Ignore
	public void test_10_NewTaipeiOpenDataService_getNewestParkingLotAvailable() {
		newTaipeiOpenDataService.getNewestParkingLotAvailable();
	}
	
	@Test
	@Ignore
	public void test_11_ViewshowMovieService_getXinyiMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	@Ignore
	public void test_12_ShowTimeMovieService_getBanqiaoShowTimeMovieTimes() {
		System.out.println("Check the system init log");
	}
	
	@Test
	@Ignore
	public void test_13_ViewshowMovieService_queryByTheaterNameAndFilmNameLike() {
		String queryResult = viewshowMovieService.queryMovieTimesByTheaterNameAndFilmNameLike("板橋大遠百威秀", "金剛");
		System.out.println(queryResult);
	}
	
	@Test
	@Ignore
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
	}
	
}
