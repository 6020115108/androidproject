package com.bighole.map.support.amap;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

public class AMapManager {

    private static final AMapManager INSTANCE = new AMapManager();

    public static AMapManager getDefault(){
        return INSTANCE;
    }

    private AMapLocationClientOption locationOption = null;
    private AMapLocationClient locationClient;

    public void init(){

    }

}
