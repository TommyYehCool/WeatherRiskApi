package com.weatherrisk.test;

import java.io.IOException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <pre>
 * 參考: <a href="http://www.journaldev.com/7207/google-search-from-java-program-example">google-search-from-java-program-example</a>
 * </pre>
 * 
 * @author tommy.feng
 *
 */
public class Test_GoogleSearchWithJSoup {

	private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";

	private static final String PREFIX = "/url?q=";

	public static void main(String[] args) throws IOException {
		// Taking search term input from console
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the search term.");
		String searchTerm = scanner.nextLine();
		System.out.println("Please enter the number of results. Example: 5 10 20");
		int num = scanner.nextInt();
		scanner.close();

		String searchURL = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num=" + num;
		// without proper User-Agent, we will get 403 error
		Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

		// below will print HTML data, save it to a file and open in browser to compare
		// System.out.println(doc.html());

		// If google search results HTML change the <h3 class="r" to <h3 class="r1"
		// we need to change below accordingly
		Elements results = doc.select("h3.r > a");

		for (Element result : results) {
			String linkText = result.text();
			String linkHref = result.attr("href");
			System.out.println("文字: " + linkText); 
			System.out.println("URL: " + linkHref.substring(linkHref.indexOf(PREFIX) + PREFIX.length(), linkHref.indexOf("&")));
		}
	}
}
