package com.bighole.map.support;


import com.bighole.map.LocModel;

import java.util.List;

public interface OnPoiSearchCallback {

    void onFinish(boolean isSuccess, int errorCode, List<LocModel> data, Object extra);

}
