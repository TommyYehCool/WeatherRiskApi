package com.weatherrisk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weatherrisk.api.model.Consume;
import com.weatherrisk.api.service.ConsumeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <pre>
 * 測試相關 API
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@Controller
@RequestMapping(value = "/test")
@Api(value = "TestController - 測試相關 API")
public class TestContoller {
	
	@Autowired
	private ConsumeService consumeService;
	
	/**
	 * <pre>
	 * 測試 GET by RequestParam
	 * </pre>
	 * 
	 * @param data 測試資料
	 * @return String 回傳 "Response your data: " + data
	 */
	@RequestMapping(value = "/testGetByRequestParam", method = RequestMethod.GET)
	@ApiOperation(value = "測試 GET by RequestParam")
	public @ResponseBody String testGetByRequestParam(@RequestParam(value = "data", required = true) String data) {
		return "Response your data: " + data;
	}
	
	@RequestMapping(value = "/testQueryConsumesByProdName", method = RequestMethod.GET)
	@ApiOperation(value = "測試根據商品名稱, 查詢消費資料", responseContainer = "List", response = Consume.class)
	public @ResponseBody List<Consume> testQueryConsumesByProdName(@RequestParam(value = "prodName", required = true) String prodName) {
		return consumeService.queryConsumesByProdName(prodName);
	}
	
	@RequestMapping(value = "/testQueryConsumesByAmountBetween", method = RequestMethod.GET)
	@ApiOperation(value = "測試根據消費金額區間, 查詢消費資料", responseContainer = "List", response = Consume.class)
	public @ResponseBody List<Consume> testQueryConsumesByAmountBetween(
		@RequestParam(value = "amountGT", required = true) Long amountGT, 
		@RequestParam(value = "amountLT", required = true) Long amountLT) {
		return consumeService.queryConsumesByAmountBetween(amountGT, amountLT);
	}
}
