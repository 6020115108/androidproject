package com.bighole.map;

import java.io.Serializable;

/**
 * 代表一个位置
 */
public class LocModel implements Serializable {

    private double lnt;
    private double lat;
    private String address;
    private String areaCode;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String city;

    /** 定位时间 */
    private long time;


    public double getLnt() {
        return lnt;
    }

    public void setLnt(double lnt) {
        this.lnt = lnt;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
