package com.weatherrisk.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.util.StringUtils;

public class GetFileUtil {

	public static String readGzFromInternet(String url) throws IOException {
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
}
