package com.bighole.map.support.amap;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bighole.map.LocModel;
import com.bighole.map.location.LocCenter;
import com.bighole.map.location.LocationConfig;
import com.bighole.map.support.OnPoiSearchCallback;


import java.util.ArrayList;
import java.util.List;

public class LocationProviderByAMap {

    private static final LocationProviderByAMap INSTANCE = new LocationProviderByAMap();

    public static LocationProviderByAMap getDefault(){
        return INSTANCE;
    }

    private AMapLocationClientOption locationOption = null;
    private AMapLocationClient locationClient;
    private boolean isRunning = false;


    public void startLocation(Context context, LocationConfig config){
        if(isRunning) {
            return;
        }
        if (null == locationOption) {
            locationOption = getDefaultOption();
        }

        switch (config.getAccuracyType()) {
            case 1:
                locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                break;
            case 2:
                locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                break;
            case 3:
                locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                break;
            default:
                break;
        }

        locationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);


        //根据控件的选择，重新设置定位参数
        resetOption(config);

        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        // 启动定位
        locationClient.startLocation();
        isRunning = true;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){

                    LocModel loc = new LocModel();
                    loc.setLnt(location.getLongitude());
                    loc.setLat(location.getLatitude());
                    loc.setAddress(location.getAddress());
                    loc.setAreaCode(location.getAdCode());
                    loc.setCity(location.getCity());
                    loc.setTime(location.getTime());
                    LocCenter.getDefault().refreshLatestLocation(loc);

//
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
//                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                    Log.w("location", sb.toString());
                } else {
                    //定位失败
                    LocCenter.getDefault().notifyError(location.getErrorCode(), location.getErrorInfo() + ", " + location.getLocationDetail());
//                    sb.append("定位失败" + "\n");
//                    sb.append("错误码:" + location.getErrorCode() + "\n");
//                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
//                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
//                sb.append("***定位质量报告***").append("\n");
//                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
//                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
//                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
//                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
//                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
//                sb.append("****************").append("\n");
//                //定位之后的回调时间
//                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
//
//                //解析定位结果，
//                String result = sb.toString();
//                tvResult.setText(result);
            } else {
                LocCenter.getDefault().notifyError(-1, "未知错误");

//                tvResult.setText("定位失败，loc is null");
            }
        }
    };

    // 根据控件的选择，重新设置定位参数
    private void resetOption(LocationConfig config) {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(config.isNeedAddress());
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(config.isGpsFirst());
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(config.isEnableLocationCache());
        // 设置是否单次定位
        locationOption.setOnceLocation(config.isLocationOnce());
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(config.isOnceLocationLatest());
        //设置是否使用传感器
        locationOption.setSensorEnable(config.isSensorEnable());
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption.setInterval(config.getInterval());

        locationOption.setHttpTimeOut(config.getHttpTimeout());
    }


    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    public void stopLocation(){
        try {
            locationClient.stopLocation();
            isRunning = false;
        }catch (Exception e){

        }
    }

//    public void release(){
//        if (null != locationClient) {
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            locationClient.stopLocation();
//            locationClient.onDestroy();
//            locationClient = null;
//            locationOption = null;
//        }
//        isRunning = false;
//    }


    public void searchPoi(Context tag, int page, int count, String keyword, double lat, double lnt, final OnPoiSearchCallback callback){
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        PoiSearch.Query query = new PoiSearch.Query(keyword, "", "");
        query.setPageSize(count);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);//设置查询页码
        final PoiSearch search = new PoiSearch(tag, query);
        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lnt), 1000)); //设置周边搜索的中心点以及半径
        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener(){

            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                if(rCode == 1000){
                    List<LocModel> list = new ArrayList<>();
                    if(poiResult != null && poiResult.getPois() != null){
                        for (PoiItem p: poiResult.getPois()){
                            LocModel loc = new LocModel();
                            loc.setLat(p.getLatLonPoint().getLatitude());
                            loc.setLnt(p.getLatLonPoint().getLongitude());
                            loc.setTitle(p.getTitle());
                            loc.setAddress(p.getSnippet());
                            loc.setAreaCode(p.getAdCode());
                            list.add(loc);
                        }
                    }
                    callback.onFinish(true, 0, list, poiResult);
                }else{
                    callback.onFinish(false, rCode, null, null);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int rCode) {

            }
        });
        search.searchPOIAsyn();
    }
}
