package com.weatherrisk.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

public class Test_GetStockPrice {
	/**
	 * <pre>
	 * <a href="http://kentyeh.blogspot.tw/2015/07/blog-post_16.html">參考網站</a>
	 * <a href="https://sites.google.com/site/kentyeh2000/zheng-jiao-suo-ji-shi-zi-xun-jie-xi">Json 格式</a>
	 * </pre> 
	 */
	@Test
	public void test() throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet("http://mis.twse.com.tw/stock");
		HttpResponse response = client.execute(request);

		String setCookie = response.getFirstHeader("Set-Cookie").getValue();
		String jsessionId = setCookie.split("; ")[0];
		
		String ex_ch = "otc_3088.tw";
//		String ex_ch = "tse_1101.tw";
		long systemTime = System.currentTimeMillis();
		String url = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?json=1&delay=0&ex_ch={0}&_=" + systemTime;
		url = MessageFormat.format(url, ex_ch);
		
		request = new HttpGet(url);
		request.addHeader("Cookie", jsessionId);
		
		response = client.execute(request);
		
		HttpEntity entity = response.getEntity();
		
		String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		json = json.replaceAll("\r\n", "").trim();
		System.out.println(json);
	}
	
	@Test
	@Ignore
	public void testGetTseStockInfo() throws Exception {
		String tseUrl = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";

		Document document 
			= Jsoup.connect(tseUrl)
				.header("Accept-Encoding", "gzip, deflate")
			    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
			    .maxBodySize(0)
			    .timeout(600000)
			    .get();

		Elements elmTableBody = document.select("table.h4 > tbody");
		Elements elmTrs = elmTableBody.select("tr");
		
		System.out.println(elmTrs.size());
		
		Iterator<Element> itElmTrs = elmTrs.iterator();
		
		while (itElmTrs.hasNext()) {
			Element elmTr = itElmTrs.next();

			String firstTdText = elmTr.select("td").first().text();
			
			String[] splitBySpace = firstTdText.split("　");
			if (splitBySpace.length != 2) {
				continue;
			}
		}
	}
	
	@Test
	public void testGetOtcStockInfo() throws Exception {
		@SuppressWarnings("unused")
		String otcUrl = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
	}
}
