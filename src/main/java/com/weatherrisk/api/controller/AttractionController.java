package com.weatherrisk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weatherrisk.api.model.Attraction;
import com.weatherrisk.api.model.AttractionType;
import com.weatherrisk.api.service.AttractionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <pre>
 * 景點相關 API
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@Controller
@RequestMapping(value = "/test")
@Api(value = "AttractionController - 景點相關 API")
public class AttractionController {
	
	@Autowired
	private AttractionService attractionService;
	
	@RequestMapping(value = "queryAttractionsByType", method = RequestMethod.GET)
	@ApiOperation(value = "根據景點類別, 查詢景點資料", responseContainer = "List", response = Attraction.class)
	public @ResponseBody List<Attraction> queryAttractionsByType(@RequestParam(value = "attractionType", required = true) AttractionType type) {
		return attractionService.queryAttrationsByType(type);
	}
}
