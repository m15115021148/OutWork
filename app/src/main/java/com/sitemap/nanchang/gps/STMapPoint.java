package com.sitemap.nanchang.gps;

/**  存储经纬的pojo对象
 *
 * @author shenqiang
 *
 */
public class STMapPoint {
	private double lng; // 经度
	private double lat; // 纬度

	/**
	 *
	 * @param lng 经度
	 * @param lat 纬度
	 */
	public STMapPoint(double lng,double lat){
		this.lng=lng;
		this.lat=lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return "STMapPoint [lng=" + lng + ", lat=" + lat + "]";
	}

}
