package com.weatherrisk.api.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherrisk.api.config.CwbConfig;
import com.weatherrisk.api.util.HttpUtil;
import com.weatherrisk.api.vo.xml.cwb.CwbOpenData;
import com.weatherrisk.api.vo.xml.cwb.Dataset;
import com.weatherrisk.api.vo.xml.cwb.DatasetInfo;
import com.weatherrisk.api.vo.xml.cwb.Location;
import com.weatherrisk.api.vo.xml.cwb.Parameter;
import com.weatherrisk.api.vo.xml.cwb.ParameterSet;

@Service
public class CwbService {
	
	private Logger logger = LoggerFactory.getLogger(CwbService.class);

	@Autowired
	private CwbConfig cwbConfig;
	
	public String getWeatherLitteleHelperByCity(String city) {
		try {
			JAXBContext jaxbContext 
				= JAXBContext.newInstance(
						new Class[] {
							CwbOpenData.class, Dataset.class, DatasetInfo.class, Location.class, ParameterSet.class, Parameter.class
						}
				  );
			
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			String url = cwbConfig.getLittleHelperUrlByCity(city);
			if (url == null) {
				return "請輸入正確城市名稱";
			}
			
			logger.info("----> Prepare to get weather little helper with city: <{}> from url: <{}>", city, url);
			
			// 這種寫法本機跑沒問題, 但放到  Heroku 會壞掉, maybe is the JDK version problem, heroku use OpenJDK
//			CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(new URL(url));

			String xmlContent = HttpUtil.getWeatherContentFromCwb(url);
			logger.info("<---- Got response, xml content: {}", xmlContent);

			StringReader reader = new StringReader(xmlContent);
			CwbOpenData data = (CwbOpenData) unmarshaller.unmarshal(reader);
			
			logger.info("----- Unmarshal to CwbOpenData result: {}", data);
			
			if (data.getDataset() == null) {
				return "不好意思, 程式寫太爛, 壞掉啦!";
			}
			
			Dataset dataset = data.getDataset();
			ParameterSet parameterSet = dataset.getParameterSet();

			StringBuilder buffer = new StringBuilder();
			buffer.append(dataset.getLocation().getLocationName()).append("-");
			
			buffer.append(parameterSet.getParameterSetName()).append("\n\n");
			
			List<Parameter> parameters = parameterSet.getParameter();
			for (Parameter parameter : parameters) {
				buffer.append(parameter.getParameterValue()).append("\n");
			}
			
			return buffer.toString();

		} catch (IOException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		} catch (JAXBException e) {
			logger.error("IOException raised while trying to get weather information", e);
			return "抓取資料發生錯誤";
		}
	}
}
