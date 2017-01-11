package com.weatherrisk.api.exception;

import com.weatherrisk.api.cnst.ResultCode;

/**
 * <pre>
 * 自訂操作系統產生的例外
 * </pre>
 * 
 * @author tommy.feng
 *
 */
public class OperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ResultCode resultCode;
	private String errorMessage;
	
	public OperationException(ResultCode resultCode) {
		this.resultCode = resultCode;
		this.errorMessage = resultCode.getMessage();
	}

	public OperationException(ResultCode resultCode, String errorMessage) {
		this.resultCode = resultCode;
		this.errorMessage = resultCode.getMessage() + ", error-msg: " + errorMessage;
	}

	public ResultCode getErrorCode() {
		return resultCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("error-code: ").append(resultCode).append(", ")
			  .append("error-msg: ").append(errorMessage);
		return buffer.toString();
	}
}
