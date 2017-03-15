package com.weatherrisk.api.vo.json.tpeopendata.ubike;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weatherrisk.api.vo.json.deserializer.UBikeAllInfoDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = UBikeAllInfoDeserializer.class)
@Data
@NoArgsConstructor
public class UBikeAllInfo {

	private List<UBikeInfo> ubikeInfos = new ArrayList<>();
	
	public void addUBikeInfo(UBikeInfo ubikeInfo) {
		this.ubikeInfos.add(ubikeInfo);
	}
}
