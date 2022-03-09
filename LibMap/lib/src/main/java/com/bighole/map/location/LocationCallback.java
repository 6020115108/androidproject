package com.bighole.map.location;

public interface LocationCallback {
    /**
     * 定位信息刷新，位置不一定变，但定位接口刷新了一次
     */
    void onLocationTick(boolean isSuccess, int errorCode, String error);

}
