package com.bighole.map.location;

public class LocationConfig {

    private boolean needAddress = false;

    /**
     * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
     * 注意：只有在高精度模式下的单次定位有效，其他方式无效
     */
    private boolean gpsFirst = true;

    // 设置是否开启缓存
    private boolean enableLocationCache = false;

    // 设置是否单次定位
    private boolean locationOnce = false;

    //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
    private boolean onceLocationLatest = false;

    //设置是否使用传感器
    private boolean sensorEnable = false;

    // 刷新间隔
    private long interval = 5000;

    private long httpTimeout = 30*1000;


    private int accuracyType = 3; //1  Battery_Saving   2 Device_Sensors 3 Hight_Accuracy

    public boolean isNeedAddress() {
        return needAddress;
    }

    public void setNeedAddress(boolean needAddress) {
        this.needAddress = needAddress;
    }

    public boolean isGpsFirst() {
        return gpsFirst;
    }

    public void setGpsFirst(boolean gpsFirst) {
        this.gpsFirst = gpsFirst;
    }

    public boolean isEnableLocationCache() {
        return enableLocationCache;
    }

    public void setEnableLocationCache(boolean enableLocationCache) {
        this.enableLocationCache = enableLocationCache;
    }

    public boolean isLocationOnce() {
        return locationOnce;
    }

    public void setLocationOnce(boolean locationOnce) {
        this.locationOnce = locationOnce;
    }

    public boolean isOnceLocationLatest() {
        return onceLocationLatest;
    }

    public void setOnceLocationLatest(boolean onceLocationLatest) {
        this.onceLocationLatest = onceLocationLatest;
    }

    public boolean isSensorEnable() {
        return sensorEnable;
    }

    public void setSensorEnable(boolean sensorEnable) {
        this.sensorEnable = sensorEnable;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getHttpTimeout() {
        return httpTimeout;
    }

    public void setHttpTimeout(long httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public int getAccuracyType() {
        return accuracyType;
    }

    public void setAccuracyType(int accuracyType) {
        this.accuracyType = accuracyType;
    }

}
