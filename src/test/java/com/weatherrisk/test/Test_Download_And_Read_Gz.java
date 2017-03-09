package com.weatherrisk.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Test_Download_And_Read_Gz {
	
	private final String GET_FILE_URL = "https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz";
	
	private final String FILE_LOCATION = "D://ParkingLotInfos.gz";
	
	/**
	 * <pre>
	 * 參考: <a href="http://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java">Download and save a file from internet</a>
	 * </pre>
	 */
	@Test
	public void testDownloadAndReadGz() {
		File outputFile = new File(FILE_LOCATION);
		try {
			if (outputFile.exists()) {
				outputFile.delete();
			}
			outputFile.createNewFile();

			FileUtils.copyURLToFile(new URL(GET_FILE_URL), new File(FILE_LOCATION));
			
//			readGzByApache();
			
			readGzByJava();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * 參考: <a href="http://stackoverflow.com/questions/25749550/read-tar-gz-in-java-with-commons-compression">Read tar/gz in java</a>
	 * </pre>
	 */
	private void readGzByApache() {
		TarArchiveInputStream tarInput = null;
		BufferedReader br = null;
		try {
			tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(FILE_LOCATION)));
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
	private void readGzByJava() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(FILE_LOCATION))));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
		}
	}
}
