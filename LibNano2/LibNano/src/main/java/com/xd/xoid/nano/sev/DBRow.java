package com.xd.xoid.nano.sev;


import java.util.HashMap;

public class DBRow extends HashMap<String, Object> {

    public static DBRow array(){
        return new DBRow();
    }

    public DBRow add(String k, Object v){
        put(k, v);
        return this;
    }

    public Object getData(String k, Object defaultValue){
        if(containsKey(k)){
            return get(k);
        }
        return defaultValue;
    }

    public int getInt(String k, int defaultValue){
        Object v = get(k);
        if(v == null) return defaultValue;
        if(v instanceof String) return Integer.parseInt(v.toString());
        return (Integer)v;
    }

    public boolean getBool(String k, boolean defaultValue){
        Object v = get(k);
        if(v == null) return defaultValue;
        return (Boolean)v;
    }

    public long getLong(String k, long defaultValue){
        Object v = get(k);
        if(v == null) return defaultValue;
        if(v instanceof String) return Long.parseLong(v.toString());
        return (Long)v;
    }

    public String getString(String k, String defaultValue){
        Object v = get(k);
        if(v == null) return defaultValue;
        return v.toString();
    }

    public void incrementInt(String key, int num){
        int current = 0;
        if(containsKey(key)){
            current = (int) get(key);
        }
        put(key, current + num);
    }
    public void incrementLong(String key, long num){
        long current = 0;
        if(containsKey(key) && get(key) != null){
            current = (long) get(key);
        }
        put(key, current + num);
    }
}
