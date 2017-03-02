package com.sitemap.nanchang.gps;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.model.GpsModel;
import com.sitemap.nanchang.util.DateUtil;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.ParserUtil;

import java.io.File;
import java.util.Iterator;

/**
 * 利用手机GPS模块获取当前位置 Description:
 *
 * @author chenhao
 * @date 2016-8-12
 */
@SuppressWarnings("MissingPermission")
public class GpsLocation {
	private LocationManager manager;// 定位管理器
	private Location location;// 位置数据
	private GpsLocationListener listener;// 监听者
	private boolean isStart = false;// 定位是否正确开启
	private Context context;// 上下文
	private int TIME = MyApplication.gpsTime;//获取位置时间频率
	private final int DISTANCE=2;//获取位置移动距离
	private double lat;
	private double lng;

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

	private String gpsPath = "";//路径

	public String getGpsPath() {
		return gpsPath;
	}

	public void setGpsPath(String gpsPath) {
		this.gpsPath = gpsPath;
	}

	public int getTIME() {
		return TIME;
	}

	public void setTIME(int TIME) {
		this.TIME = TIME;
	}

	public GpsLocation(Context context) {
		this.context = context;

	}

	/**
	 * 单例模式，获取唯一实例
	 *
	 * @param context
	 */
	private void getInstance(Context context) {
		if (manager == null) {
			manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}
		if (location == null) {
			// 从GPS_PROVIDER获取最近的定位信息
			location = manager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (listener == null) {
			// 从GPS_PROVIDER获取最近的定位信息
			listener = new GpsLocationListener();
		}
	}

	/**
	 * 判断GPS是否可用
	 *
	 * @return
	 */
	public boolean isGPSEnable() {
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 获取最近一次的位置
	 */
	public STMapPoint getPositionByGps() {

		if (!isStart) {
			Toast.makeText(context, "The GPS Service is not open !", Toast.LENGTH_SHORT)
					.show();
			return null ;
		}

		location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			Log.i("will", "The location that we get is null .");
			return null;
		}
		try {
			STMapPoint point = new STMapPoint(location.getLongitude(), location.getAltitude());
			return point;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 开启定位服务
	 */
	public void start() {
		getInstance(context);
		if (!isGPSEnable()) {
//			Toast.makeText(context, "The GPS is not open !", Toast.LENGTH_SHORT)
//					.show();
			openGPSSettings();
			return;
		}
		// 设置每60秒，每移动十米向LocationProvider获取一次GPS的定位信息
		if (!isStart) {

			// 查找到服务信息
			Criteria criteria=new Criteria();
			//设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			//设置是否要求速度
			criteria.setSpeedRequired(false);
			// 设置是否允许运营商收费
			criteria.setCostAllowed(true);
			//设置是否需要方位信息
			criteria.setBearingRequired(false);
			//设置是否需要海拔信息
			criteria.setAltitudeRequired(false);
			// 设置对电源的需求
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			String provider = manager.getBestProvider(criteria, true); // 获取GPS信息
			Location location = manager.getLastKnownLocation(provider); // 通过GPS获取位置

			manager.addGpsStatusListener(gpslistener);
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME,
					DISTANCE, listener);
			isStart = true;
		}
	}

	/**
	 * 关闭定位服务
	 */
	public void close() {
		if (isStart) {
			if (manager != null) {
				manager.removeUpdates(listener);
				manager.removeGpsStatusListener(gpslistener);
				manager = null;
			}

			if (listener != null) {
				listener = null;
			}
		}
		isStart = false;
	}

	//状态监听
	GpsStatus.Listener gpslistener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
				//第一次定位
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					Log.i("result", "第一次定位");
					break;
				//卫星状态改变
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					Log.i("result", "卫星状态改变");
					//获取当前状态
					GpsStatus gpsStatus = manager.getGpsStatus(null);
					//获取卫星颗数的默认最大值
					int maxSatellites = gpsStatus.getMaxSatellites();
					//创建一个迭代器保存所有卫星
					Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
					int count = 0;
					while (iters.hasNext() && count <= maxSatellites) {
						GpsSatellite s = iters.next();
						count++;
					}
					Log.i("result","搜索到："+count+"颗卫星");
					break;
				//定位启动
				case GpsStatus.GPS_EVENT_STARTED:
					Log.i("result", "定位启动");
					break;
				//定位结束
				case GpsStatus.GPS_EVENT_STOPPED:
					Log.i("result", "定位结束");
					break;
			}
		};
	};

	/**
	 * 得到经纬度
	 * @return
	 */
	public double[] getLatLng(){
		double[] d = new double[2];
		if (location!=null){
			d[0] = location.getLatitude();
			d[1] = location.getLongitude();
		}else{
			d[0] = 0.0;
			d[1] = 0.0;
		}
		return d;
	}

	public class GpsLocationListener implements LocationListener {

		@Override
		public void onProviderEnabled(String provider) {
			LocationLog(manager.getLastKnownLocation(provider));
		}

		@Override
		public void onProviderDisabled(String provider) {
			LocationLog(null);
		}

		@Override
		public void onLocationChanged(Location location) {
			// location为变化完的新位置，更新显示
			LocationLog(location);
		}

		//		卫星状态发生改变
		@Override
		public void onStatusChanged(String provider, int status,
									Bundle extras) {

		}

		private void LocationLog(Location location) {
			if (location == null) {
				Log.i("will", "location is null");
				return;
			}
			Log.i("will", "时间："+location.getTime());
			Log.i("will", "经度："+location.getLongitude());
			Log.i("will", "纬度："+location.getLatitude());
			Log.i("will", "海拔："+location.getAltitude());

			lat = location.getLatitude();
			lng = location.getLongitude();
			GpsModel gpsModel = new GpsModel();
			gpsModel.setLat(String.valueOf(lat));
			gpsModel.setLng(String.valueOf(lng));
			gpsModel.setTime(DateUtil.getCurrentDate());
			if (!gpsPath.equals("") && lat != 4.9E-324 && lat != 0 & lng != 0) {
				File gpsFile = new File(gpsPath);
				FileUtil.saveFilePath(gpsFile, RequestCode.GPSTXT, ParserUtil.objectToJson(gpsModel) + "\r\n", true);
			}
		}
	}


	/**
	 * 自动打开gps
	 */
	private void openGPSSettings() {
		LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
		{
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提示");
			builder.setMessage("是否开启GPS？");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						context.startActivity(intent);
					} catch(ActivityNotFoundException ex) {
						intent.setAction(Settings.ACTION_SETTINGS);
						try {
							context.startActivity(intent);
						} catch (Exception e) {
						}
					}
				}
			});
			builder.setNegativeButton("否",null);
			builder.show();
		}
	}

}
