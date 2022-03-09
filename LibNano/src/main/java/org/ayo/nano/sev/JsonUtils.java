package org.ayo.nano.sev;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.ayo.nano.InnerUtils;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

	private JsonUtils() {
	}

	public static <T> T parse2(String jsonString, TypeReference<T> typeReference) {
		T t = JSON.parseObject(jsonString, typeReference);
		return t;
	}

	public static AssocArray2 parse(String json){
		return parse(json, AssocArray2.class);
	}

	public static <T> T parse(String json, Class<T> clazz){
		if(InnerUtils.isEmpty(json)) return null;
		if("{}".equals(json)) return null;
		T t = null;
		t = JSON.parseObject(json, clazz);
		return t;
	}

	public static List<AssocArray2> parseList(String json){
		try {
			return parseList(json, AssocArray2.class);
		}catch (Exception e){
			return new ArrayList<>();
		}
	}

	public static String toJson(Object bean){

		if(bean == null){
			return "{}";
		}
		return JSON.toJSONString(bean);
	}
	public static String toJsonPretty(Object bean){

		if(bean == null){
			return "{}";
		}
		return JSON.toJSONString(bean, true);
	}

	public static <T> List<T> parseList(String jsonArrayString, Class<T> cls) {
		try {
			List<T> beanList = JSON.parseArray(jsonArrayString, cls);
			return beanList;
		}catch (Exception e){
			return null;
		}

	}


}
