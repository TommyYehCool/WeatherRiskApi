package com.weatherrisk.test;

import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.weatherrisk.api.util.HttpUtil;

public class Test_GetDocument {

	@Test
	public void testGetWovieCinemas() {
		String url = "https://tienmou.woviecinemas.com.tw/movie_wovie.php";
		try {
			Document document = HttpUtil.getDocument(url);
			Iterator<Element> itFilmTables = document.select("table[align=\"center\"]").iterator();
			while (itFilmTables.hasNext()) {
				Element filmTable = itFilmTables.next();
				String filmName = filmTable.select("span.style4").first().text();
				
				System.out.println(filmName);
				System.out.println();
				
				Iterator<Element> itTimesTables = filmTable.select("table[height=100%]").iterator();
				while (itTimesTables.hasNext()) {
					Element elmTimesTable = itTimesTables.next();
					
					Iterator<Element> itElmTimes = elmTimesTable.select("td.text-big1").iterator();
					while (itElmTimes.hasNext()) {
						String time = itElmTimes.next().text();
						time = time.substring(1, time.lastIndexOf("|") - 1);
						System.out.println(time);
					}
				}
				System.out.println("---------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
