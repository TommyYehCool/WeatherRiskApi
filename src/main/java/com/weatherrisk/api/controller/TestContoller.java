package com.weatherrisk.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
