package com.weatherrisk.api.vo.response;

import com.weatherrisk.api.cnst.ResultCode;
import com.weatherrisk.api.exception.OperationException;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <pre>
 * 統一回應前端格式 
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@ApiModel(description = "操作回應")
public class RespCommon {
	@ApiModelProperty(notes = "操作代碼")
	private ResultCode resultCode;
	
	@ApiModelProperty(notes = "回應資料")
	private Object data;

	public RespCommon(ResultCode resultCode, Object data) {
		this.resultCode = resultCode;
		this.data = resultCode.getMessage() + ", error-msg: " + data;
	}
	
	public RespCommon(ResultCode resultCode) {
		this.resultCode = resultCode;
		this.data = resultCode.getMessage();
	}
	
	public RespCommon(OperationException ex) {
		this.resultCode = ex.getErrorCode();
		this.data = ex.getErrorMessage();
	}

	public int getResultCode() {
		return resultCode.getCode();
	}

	public Object getData() {
		return data;
	}
}
