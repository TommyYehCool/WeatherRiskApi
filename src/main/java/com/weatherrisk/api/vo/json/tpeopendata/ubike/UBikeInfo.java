package com.weatherrisk.api.vo.json.tpeopendata.ubike;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * UBike 資訊
 * 
 * 參考: <a href="http://data.taipei/opendata/datalist/datasetMeta?oid=8ef1626a-892a-4218-8344-f7ac46e1aa48">YouBike臺北市公共自行車即時資訊</a>
 * </pre>
 * 
 * @author tommy.feng
 */
@Data
@NoArgsConstructor
public class UBikeInfo {
	/**
	 * 站點代號
	 */
	private String sno;
	/**
	 * 場站名稱(中文)
	 */
	private String sna;
	/**
	 * 場站總停車格
	 */
	private Integer tot;
	/**
	 * 場站目前車輛數量
	 */
	private Integer sbi;
	/**
	 * 場站區域(中文)
	 */
	private String sarea;
	/**
	 * 資料更新時間
	 */
	private String mday;
	/**
	 * 緯度
	 */
	private Double lat;
	/**
	 * 經度
	 */
	private Double lng;
	/**
	 * 地(中文)
	 */
	private String ar;
	/**
	 * 場站區域(英文)
	 */
	private String sareaen;
	/**
	 * 場站名稱(英文)
	 */
	private String snaen;
	/**
	 * 地址(英文)
	 */
	private String aren;
	/**
	 * 空位數量
	 */
	private Integer bemp;
	/**
	 * 全站禁用狀態
	 */
	private Integer act;
}
