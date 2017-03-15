package com.weatherrisk.api.vo.json.tpeopendata.ubike;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UBikeAllInfo {

	private List<UBikeInfo> ubikeInfos = new ArrayList<>();
	
	public void addUBikeInfo(UBikeInfo ubikeInfo) {
		this.ubikeInfos.add(ubikeInfo);
	}
}
