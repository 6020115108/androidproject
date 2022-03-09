package com.bighole.map.location;

import android.content.Context;


import com.bighole.map.LocModel;
import com.bighole.map.support.amap.LocationProviderByAMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocCenter {

    private LocCenter() {
    }

    private static final class H {
        private static final LocCenter I = new LocCenter();
    }

    public static LocCenter getDefault() {
        return H.I;
    }

    private LocationProviderByAMap locationProvider;
    private LocationConfig config;

//    private LocModel mainLocation;
    private LocModel latestLocation;
//    private List<LocModel> locations = new ArrayList<>();
    private Map<Integer, LocationCallback> callbacks = new ConcurrentHashMap<>();

    public void setConfig(LocationConfig config) {
        this.config = config;
        this.locationProvider = new LocationProviderByAMap();
    }

    //    public LocModel getMainLocation() {
//        return mainLocation;
//    }

    public LocModel getLatestLocation(){
//        if(locations.size() == 0){
//            return mainLocation;
//        }else{
//            return locations.get(locations.size()-1);
//        }
        return latestLocation;
    }

    public void register(Object tag, LocationCallback callback){
        callbacks.put(tag.hashCode(), callback);
    }

    public void unregister(Object tag){
        callbacks.remove(tag.hashCode());
    }

    private void notifyOk(){
        for (Integer k: callbacks.keySet()){
            callbacks.get(k).onLocationTick(true, 0, null);
        }
    }

    public void notifyError(int code, String error){
        for (Integer k: callbacks.keySet()){
            callbacks.get(k).onLocationTick(false, code, error);
        }
    }

    public void refreshLatestLocation(LocModel loc){
//        if(loc == null) return;
//        if(mainLocation == null) mainLocation = loc;
//        locations.add(loc);
        latestLocation = loc;
        notifyOk();
    }

//    void refreshMainLocation(LocModel loc){
//        mainLocation = loc;
//        notifyCallback();
//    }

    /**
     *
     */
    public void start(Context context){
        locationProvider.startLocation(context, config);
//        if(useCache && latestLocation != null && callback != null){
//            callback.onLocationTick(true, 0, null);
//        }
    }

    public void stop(){
        locationProvider.stopLocation();
    }

//    public void release(){
//        locationProvider.release();
//    }

}
