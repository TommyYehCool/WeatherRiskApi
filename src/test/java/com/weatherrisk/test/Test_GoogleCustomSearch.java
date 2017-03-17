package com.weatherrisk.test;

import java.util.List;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

/**
 * <pre>
 * 參考: <a href="https://developers.google.com/api-client-library/java/apis/customsearch/v1">Google Api 網站</a>
 * 
 * 參考: <a href="https://www.imarc.com/blog/google-custom-search">API Key 取法</a>
 * 
 * 參考: <a href="http://www.programcreek.com/java-api-examples/index.php?api=com.google.api.services.customsearch.Customsearch">用法參考</a>
 * </pre>
 * 
 * @author tommy.feng
 *
 */
public class Test_GoogleCustomSearch {
	
	private String API_KEY = "";
	private String CSE_ID = "";

	public List<Result> search(String keyword) {
		Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
		List<Result> resultList = null;
		try {
			Customsearch.Cse.List list = customsearch.cse().list(keyword);
			list.setKey(API_KEY);
			list.setCx(CSE_ID);
			Search results = list.execute();
			resultList = results.getItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static void main(String[] args) {
		Test_GoogleCustomSearch instance = new Test_GoogleCustomSearch();
		List<Result> results = instance.search("繪色千佳");
		System.out.println(results);
	}

}
