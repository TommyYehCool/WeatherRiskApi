package com.weatherrisk.test;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.xml.cwb.CwbOpenData;

public class Test_Unmarshaller {

	@Test
	public void testUnmarshaller() throws JAXBException, IOException {
		JAXBContext jaxbContext 
			= JAXBContext.newInstance(CwbOpenData.class);
	
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	
		String url = "http://opendata.cwb.gov.tw/opendataapi?authorizationkey=CWB-177B46C0-418B-4126-AC66-99C778E8CABE&dataid=F-C0032-009";
	
		String xmlContent = HttpUtil.getWeatherContentFromCwb(url);

		StringReader reader = new StringReader(xmlContent);

		CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(reader);
		
		System.out.println(data);
	}
}
