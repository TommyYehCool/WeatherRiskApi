package com.weatherrisk.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class Test_Get_Gz_From_TaipeiOpenData {
	
	private final String PARKING_LOT_INFO_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz";
	
	private final String PARKING_LOT_INFO_FILE_LOCATION = "D://ParkingLotInfo.gz";
	
	private final String PARKING_LOT_AVAILABLE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	private final String PARKING_LOT_AVAILABLE_FILE_LOCATION = "D://ParkingLotAvailable.gz";
	
	private final String UBIKE_INFO_URL = "http://data.taipei/youbike";
	
	@Test
	public void testGetUBike() {
		HttpClient client 
			= HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		HttpGet get = new HttpGet(UBIKE_INFO_URL);
		
		try {
			HttpResponse response = client.execute(get);
			
			String responseData = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			
			System.out.println(responseData);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testReadGzFromInternet() {
		readGzFromInternet(PARKING_LOT_INFO_URL);
		readGzFromInternet(PARKING_LOT_AVAILABLE_URL);
	}
	
	private void readGzFromInternet(String url) {
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
			System.out.println(buffer.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(br);
		}
	}
	
	@Test
	@Ignore
	public void testDownloadAndReadGz() {
		try {
			downloadFile(PARKING_LOT_INFO_URL, PARKING_LOT_INFO_FILE_LOCATION);
			
			readGzByJava(PARKING_LOT_INFO_FILE_LOCATION);
			
			downloadFile(PARKING_LOT_AVAILABLE_URL, PARKING_LOT_AVAILABLE_FILE_LOCATION);
			
			readGzByJava(PARKING_LOT_AVAILABLE_FILE_LOCATION);
		} catch (IOException e) {
			System.err.println("IOException rasied while trying to download file");
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * 參考: <a href="http://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java">Download and save a file from internet</a>
	 * </pre>
	 */
	private void downloadFile(String url, String fileLocation) throws IOException {
		File fileParkingLotAvailable = new File(fileLocation);
		if (fileParkingLotAvailable.exists()) {
			fileParkingLotAvailable.delete();
		}
		fileParkingLotAvailable.createNewFile();

		FileUtils.copyURLToFile(new URL(url), new File(fileLocation));
	}

	/**
	 * <pre>
	 * 參考: <a href="http://stackoverflow.com/questions/25749550/read-tar-gz-in-java-with-commons-compression">Read tar/gz in java</a>
	 * 
	 * PS: 讀取會有問題
	 * </pre>
	 */
	@SuppressWarnings("unused")
	private void readGzByApache(String fileLocation) {
		TarArchiveInputStream tarInput = null;
		BufferedReader br = null;
		try {
			tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(fileLocation)));
			TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
			while (currentEntry != null) {
			    br = new BufferedReader(new InputStreamReader(tarInput)); // Read directly from tarInput
			    System.out.println("For File = " + currentEntry.getName());
			    String line;
			    while ((line = br.readLine()) != null) {
			        System.out.println("line="+line);
			    }
			    currentEntry = tarInput.getNextTarEntry(); // You forgot to iterate to the next file
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(tarInput);
			IOUtils.closeQuietly(br);
		}
	}
	
	/**
	 * <pre>
	 * 參考: <a href="http://stackoverflow.com/questions/4991217/unzip-huge-gz-file-in-java-and-performance">Unzip gz file in java</a>
	 * </pre>
	 */
	private void readGzByJava(String fileLocation) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileLocation)), "MS950"));
			
			StringBuilder buffer = new StringBuilder();
			
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					buffer.append(line);
				}
			}
			System.out.println(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
		}
	}
}
