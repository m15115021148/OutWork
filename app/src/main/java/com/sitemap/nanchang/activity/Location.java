package com.sitemap.nanchang.activity;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.model.GpsModel;
import com.sitemap.nanchang.util.DateUtil;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.ParserUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/12/8.
 */

public class Location {
    private static Location _instance;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener;
    private double lat, lng;
    private String gpsPath = "";

    public String getGpsPath() {
        return gpsPath;
    }

    public void setGpsPath(String gpsPath) {
        this.gpsPath = gpsPath;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Location() {
        myListener = new MyLocationListener();
    }

    public static Location instance() {
        if (_instance != null) {
            return _instance;
        } else {
            _instance = new Location();
            return _instance;
        }
    }

    /**
     * 定位监听器
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.i("TAG", "lat:" + lat);
            Log.i("TAG", "lng:" + lng);
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
     * 配置定位sdk的参数
     */
    public void start(Context context) {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(myListener);
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");// 返回的定位结果包含地址信息
        // option.disableCache(false);// 禁止启用缓存定位
        // option.setPriority(LocationClientOption.GpsFirst);
        option.setScanSpan(MyApplication.gpsTime);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        mLocationClient.setLocOption(option);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    /**
     * 获取上次定位的经纬度
     *
     * @return
     */
    public double[] getLocation() {
        double[] str = new double[2];
        str[0] = 0;
        str[1] = 0;
        if(mLocationClient==null){
            return  null;
        }
        mLocationClient.requestLocation();
        str[0] = mLocationClient.getLastKnownLocation().getLongitude();
        str[1] = mLocationClient.getLastKnownLocation().getLatitude();
        if (str[0] == 0 && str[1] == 0 && str[1] == 4.9E-324) {
            mLocationClient.requestLocation();
            str[0] = 0.0;
            str[1] = 0.0;
        }
        return str;
    }

    /**
     * 关闭 定位
     */
    public void stop() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }
}
