package com.weatherrisk.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

public class HttpUtil {
	
	public static String getJsonContentFromOpenData(String url) throws IOException {
		HttpClient client 
			= HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		
		String responseData = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
		
		return responseData;
	}

	public static String getGzContentFromOpenData(String url) throws IOException {
		URL objUrl = null;
		InputStream inStream = null;
		BufferedReader br = null;
		try {
			objUrl = new URL(url);
			inStream = objUrl.openStream();
			
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(inStream), "MS950"));

			StringBuilder buffer = new StringBuilder();
			
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					buffer.append(line);
				}
			}
			return buffer.toString();

		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(br);
		}
	}
	
	public static String getWeatherContentFromCwb(String url) throws IOException {
		HttpClient client 
			= HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		
		String responseData = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
		
		return responseData;
	}
	
	private static final int CONNECTION_TIMEOUT = 5 * 1000;
	private static final String CONNECTION_USER_AGENT = "Mozilla/5.0";
	
	public static Document getDocument(String url) throws Exception {
		Connection connection = Jsoup.connect(url);
		connection.timeout(CONNECTION_TIMEOUT);
		connection.userAgent(CONNECTION_USER_AGENT);
		Document doc = connection.get();
		return doc;
	}
}
