package com.weatherrisk.api.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consumes")
public class Consume {
	
	@Id
	@ApiModelProperty(notes = "發票號碼")
	private String lotteryNo;
	
	@ApiModelProperty(notes = "使用者")
	private String user;
	
	@ApiModelProperty(notes = "消費日期")
	private Date consumeDate;
	
	@ApiModelProperty(notes = "類別")
	private Integer type;
	
	@ApiModelProperty(notes = "商品名稱")
	private String prodName;
	
	@ApiModelProperty(notes = "消費金額")
	private Long amount;
	
	@ApiModelProperty(notes = "中獎金額")
	private Long prize;
	
	@ApiModelProperty(notes = "是否中獎")
	private Boolean got;
	
	@ApiModelProperty(notes = "是否已發信")
	private Boolean alreadySent;
	
}
