package com.sitemap.nanchang.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sitemap.nanchang.R;
import com.sitemap.nanchang.activity.Location;
import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.model.GpsModel;
import com.sitemap.nanchang.util.DateUtil;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.JsonUtil;
import com.sitemap.nanchang.util.ParserUtil;

import java.io.File;

public class MapFragment extends Fragment implements View.OnClickListener{
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener ;
    private BaiduMap.OnMarkerClickListener markClick;// 地图标注点击事件
    private Marker marker;//自己的位置
    private double lat,lng;
    private View view;
    private MapFragment fragment;
    private String gpsPath="";
    Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView)view.findViewById(R.id.bmapView);
        fragment=this;
        myListener = new MyLocationListener();
        try {
            mBaiduMap = mMapView.getMap();
            // 隐藏缩放控件
            hidezoomView();
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 定位当前位置
//            mLocationClient = new LocationClient(fragment.getActivity()); // 声明LocationClient类
//            mLocationClient.registerLocationListener(myListener);
        } catch (Exception e) {
            // TODO: handle exception
        }
//        File appFilePath = FileUtil.createFolder(fragment.getContext(), RequestCode.APPNAME);//创建根目录
//        Log.i("TAG","path:"+Environment .getExternalStorageDirectory().toString()+",,,,,,,:"+Environment .getExternalStorageDirectory().getPath());
//        String FILE_PAHT =  Environment .getExternalStorageDirectory().toString() + "/";
//        File appFilePath = new File(FILE_PAHT +  RequestCode.APPNAME);
//        appFilePath.mkdir();
//        if (appFilePath.exists()){
//            File upLoadFile = FileUtil.createOneFolder(appFilePath, RequestCode.TASK);
//            if (upLoadFile.exists()){//uuid
//                File userUuidFile = FileUtil.createOneFolder(upLoadFile, MyApplication.uuid);
//                if (userUuidFile.exists()){//创建task目录 多个
//                    File gpsFile = FileUtil.createOneFolder(userUuidFile, RequestCode.GPS);
//                    if (gpsFile.exists()) {
//                        FileUtil.saveFilePath(gpsFile, RequestCode.GPSTXT, "",true);
//                        gpsPath=gpsFile.getPath();
//                    }
//                }
//            }
//        }else{
//        }

//        initLocation();
//        Log.i("TAG","aaaaaaaaaaaaaaaaaa");
//         location=Location.instance();
//        Log.i("TAG","bbbbbbbbbbbbb");
//        location.setContext(fragment.getContext());
//        Log.i("TAG","ccccccccccccccccc");
//        location.initLocation();
//        Log.i("TAG","ddddddddddddddddddd");
//        location.mLocationClient.start();
////        location.mLocationClient.requestNotifyLocation();
//        Log.i("TAG","eeeeeeeeeeeeeeeeee");
//        LatLng point = new LatLng(location.getLat(),
//                location.getLng());
//            city = location.getCity();
//        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.dingweimy);
//        // 构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(changeBaidu(point)).icon(bitmap)
//                .zIndex(2);
//        marker=(Marker)mBaiduMap.addOverlay(option);
//        updateStatus(changeBaidu(point), 15);
//        location.mLocationClient.stop();
//        Log.i("TAG","fffffffffffffffffffffffffff:"+location.mLocationClient.isStarted());
        return view;
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        location.mLocationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        Log.i("TAG", "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        location.mLocationClient.stop();
        super.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        Log.i("TAG", "onPause");
        super.onPause();

    }

    /**
     * 隐藏缩放控件
     */
    private void hidezoomView() {
        final int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }
    @Override
    public void onClick(View v) {

    }


    /**
     * 定位监听器
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            lat=location.getLatitude();
            lng=location.getLongitude();
            Log.i("TAG", "正在定位");
            Log.i("TAG", "lat:"+lat);
            Log.i("TAG", "lng:"+lng);
//            GpsModel gpsModel=new GpsModel();
//            gpsModel.setLat(String.valueOf(lat));
//            gpsModel.setLng(String.valueOf(lng));
//            gpsModel.setTime(DateUtil.getCurrentDate());
//            File gpsFile=new File(gpsPath);
//            FileUtil.saveFilePath(gpsFile, RequestCode.GPSTXT, ParserUtil.objectToJson(gpsModel),true);
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            // 这是第一次进来默认的地图
            // 定位当前位置之后，显示在地图的位置
//             setPoint(new LatLng(location.getLatitude(), location.getLongitude()),
//             location.getAddrStr(), 0);
            lat=location.getLatitude();
            lng= location.getLongitude();
            LatLng point = new LatLng(location.getLatitude(),
                    location.getLongitude());
//            city = location.getCity();
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.dingweimy);
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(changeBaidu(point)).icon(bitmap)
                    .zIndex(2);
            marker=(Marker)mBaiduMap.addOverlay(option);
            updateStatus(changeBaidu(point), 15);

        }

    }

    /**
     * update地图的状态与变化
     *
     * @param point
     * @param zoom
     */
    private void updateStatus(LatLng point, int zoom) {
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(zoom)
                .build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate =

                MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * GPS转百度坐标
     *
     * @param ll
     * @return
     */
    private LatLng changeBaidu(LatLng ll) {
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(ll);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 自定义点标记
     * @param point
     * @param adress
     * @param time
     * @param type
     */
    public void setPoint(LatLng point, String adress, String time, int type) {
        // 定义Maker坐标点
        // = new LatLng(Latitude, Longitude);

        // 创建InfoWindow展示的自定义view,显示详情对话框
        TextView button = setPop(adress);

        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.index_location);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap).zIndex(1);
        View contentView =fragment.getActivity().getLayoutInflater().inflate(R.layout.mapmark_layout,
                null);
        TextView location_time = (TextView) contentView
                .findViewById(R.id.location_time);
        TextView location_address = (TextView) contentView
                .findViewById(R.id.location_address);
        location_time.setText(time);
        location_address.setText(adress);
        location_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
        // 构建对话框用于显示
        // 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        final InfoWindow mInfoWindow = new InfoWindow(contentView, point, -80);
        if (type == 0) {
            mBaiduMap.clear();
        }

        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        // 显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
        markClick = new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                mBaiduMap.showInfoWindow(mInfoWindow);
                return false;
            }
        };
        mBaiduMap.setOnMarkerClickListener(markClick);
        // 正常显示
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        // 定义地图状态
        updateStatus(point, 15);
//        mapClear.setVisibility(View.VISIBLE);
    }

    /**
     * 自定义地图对话框，展示详情
     *
     * @param adress
     * @return
     */
    private TextView setPop(String adress) {
        TextView button = new TextView(fragment.getContext());
        button.setBackgroundColor(getResources().getColor(R.color.white));
        button.setTextSize(14);
        button.setTextColor(getResources().getColor(R.color.black));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                200, LinearLayout.LayoutParams.MATCH_PARENT);
        button.setText(adress);
        layoutParams.setMargins(50, 0, 50, 0);
        button.setLayoutParams(layoutParams);

        return button;
    }


    /**
     * 配置定位sdk的参数
     */
    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
        mLocationClient = new LocationClient(fragment.getContext());
        mLocationClient.registerLocationListener(myListener);
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");// 返回的定位结果包含地址信息
        // option.disableCache(false);// 禁止启用缓存定位
        // option.setPriority(LocationClientOption.GpsFirst);
        option.setScanSpan(1000 * 60*60*24);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        Log.i("TAG", "定位开启");
    }
}
