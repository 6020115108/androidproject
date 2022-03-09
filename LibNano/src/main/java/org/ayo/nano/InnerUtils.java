package org.ayo.nano;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class InnerUtils {
    // -----------------------------------------------------
    // 判空系列
    // -----------------------------------------------------
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Map<?, ?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> c) {
        return !isEmpty(c);
    }

    public static <T> boolean isEmpty(T[] c) {
        return c == null || c.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] c) {
        return !isEmpty(c);
    }

    public static boolean isEmpty(CharSequence c) {
        return c == null || c.toString().isEmpty();
    }

    public static boolean isNotEmpty(String c) {
        return !isEmpty(c);
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    // -----------------------------------------------------
    // 集合数组长度系列
    // -----------------------------------------------------
    public static int count(Collection<?> c) {
        return c == null ? 0 : c.size();
    }

    public static int count(Map<?, ?> c) {
        return c == null ? 0 : c.size();
    }

    public static <T> int count(T[] c) {
        return c == null ? 0 : c.length;
    }

    public static <T> int count(String s) {
        return s == null ? 0 : s.length();
    }

    public static int rcolor(int id) {
        return LibNano.app.getResources().getColor(id);
    }
    public static int rcolor(String color) {
        return Color.parseColor(color);
    }



    public static int dip2px(float dpValue) {
        final float scale = LibNano.app.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = LibNano.app.getResources().getDisplayMetrics().density;
        return (int) ((pxValue / scale + 0.5f) - 15);
    }

    public static int sp2px(float value) {
        DisplayMetrics metrics = LibNano.app.getResources().getDisplayMetrics();
        return (int) (value * metrics.scaledDensity);
    }

    public static int px2sp(float value) {
        DisplayMetrics metrics = LibNano.app.getResources().getDisplayMetrics();
        return (int) (value / metrics.scaledDensity);
    }

    public static <T> T lastElement(List<T> list) {
        if (list == null || list.size() == 0)
            return null;
        return list.get(list.size() - 1);
    }

    public static  List<Object> combineIgnoreType(List<Object> c1, List<?> c2) {
        if (c1 == null && c2 == null)
            return null;
        if(c1 == null) c1 = new ArrayList<>();
        for (int i = 0; i < count(c2); i++) {
            c1.add(c2.get(i));
        }
        return c1;
    }

    public static <T> String fromList(List<T> list, String delemeter, boolean ignoreNull){
        if(isEmpty(list)) return "";
        String res = "";
        for (int i = 0; i < list.size(); i++) {
            if(ignoreNull && list.get(i) == null) continue;
            res += snull(list.get(i), "") + (i == list.size() - 1 ? "" : delemeter);
        }
        return res;
    }

    public static String snull(Object maybeNullOrEmpty, String replaceNull) {
        return (maybeNullOrEmpty == null || "".equals(maybeNullOrEmpty)) ? replaceNull : maybeNullOrEmpty.toString();
    }

    public static void toastLong(String text){
        Toast.makeText(LibNano.app, text, Toast.LENGTH_LONG).show();
    }

    public static String urldecode(String s){
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static String readStr(InputStream in) throws IOException {
        return readStr(in, "UTF-8");
    }

    public static String readStr(InputStream in, String charset) throws IOException {
        if (TextUtils.isEmpty(charset)) charset = "UTF-8";

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) >= 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString().trim();
    }
}
