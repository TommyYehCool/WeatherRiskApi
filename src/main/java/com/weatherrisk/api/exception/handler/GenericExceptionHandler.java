package com.weatherrisk.api.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.weatherrisk.api.cnst.ResultCode;
import com.weatherrisk.api.exception.OperationException;
import com.weatherrisk.api.vo.response.RespCommon;

/**
 * <pre>
 * 統一處理系統例外用
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@ControllerAdvice
public class GenericExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(GenericExceptionHandler.class);
	
	@ExceptionHandler(OperationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RespCommon handleOperationException(OperationException ex) {
		logger.error("OperationException raised, error-code: <{}>, error-msg: <{}>", ex.getErrorCode(), ex.getErrorMessage());
		return new RespCommon(ex);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RespCommon handleAccessDeniedException(AccessDeniedException ex) {
		logger.error("AccessDeniedException raised", ex);
		return new RespCommon(ResultCode.ACCESS_DENIED, ex.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RespCommon handleAllException(Exception ex) {
		logger.error("Exception raised", ex);
		return new RespCommon(ResultCode.SYSTEM_EXCEPTION, ex.getMessage());
	}
}
