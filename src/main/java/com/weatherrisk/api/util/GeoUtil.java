package com.weatherrisk.api.util;

public class GeoUtil {
	
	/**
	 * <pre>
	 * 計算兩個經緯度距離
	 * 
	 * 參考: <a href="http://dean-android.blogspot.tw/2013/05/androidlist.html?m=1">計算經緯度距離</a>
	 * </pre>
	 */
	public static double calculateDistance(double longitude1, double latitude1, double longitude2,double latitude2) {
	   double radLatitude1 = latitude1 * Math.PI / 180;
	   double radLatitude2 = latitude2 * Math.PI / 180;
	   double l = radLatitude1 - radLatitude2;
	   double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
	   double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
	                    + Math.cos(radLatitude1) * Math.cos(radLatitude2)
	                    * Math.pow(Math.sin(p / 2), 2)));
	   distance = distance * 6378137.0;
	   distance = Math.round(distance * 10000) / 10000;

	   return distance;
	}
}
