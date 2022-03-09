package org.ayo.nano.sev;


import org.ayo.nano.InnerUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {


    public static Map<String, String>  parseQueryString(String queryString){
        Map<String,String> map = new HashMap<>();
        if(InnerUtils.isEmpty(queryString)) return map;
        String[] pairs = queryString.split("&");
        if(InnerUtils.count(pairs) == 0) return map;
        for (int i = 0; i < pairs.length; i++) {
            String[] parts = queryString.split("=");

            String value = "";
            if(parts.length == 2){
                value = InnerUtils.urldecode(parts[1]);
            }else{
                value = "";
            }

            map.put(parts[0], value);
        }
        return map;
    }

    public static boolean isImage(File file){
        Set<String> suffix = new HashSet<>();
        suffix.add("png");
        suffix.add("jpg");
        suffix.add("jpeg");
        suffix.add("gif");
        suffix.add("bmp");
        if(file.getName().indexOf('.') == -1) return false;
        String fileSuffix = file.getName().split("\\.")[1];
        for (String s: suffix){
            if(s.equalsIgnoreCase(fileSuffix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isVideo(File file){
        Set<String> suffix = new HashSet<>();
        suffix.add("mp4");
        suffix.add("avi");
        suffix.add("wmv");
        suffix.add("mkv");
        suffix.add("flv");
        if(file.getName().indexOf('.') == -1) return false;
        String fileSuffix = file.getName().split("\\.")[1];
        for (String s: suffix){
            if(s.equalsIgnoreCase(fileSuffix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isAudio(File file){
        Set<String> suffix = new HashSet<>();
        suffix.add("mp3");
        suffix.add("amr");
        if(file.getName().indexOf('.') == -1) return false;
        String fileSuffix = file.getName().split("\\.")[1];
        for (String s: suffix){
            if(s.equalsIgnoreCase(fileSuffix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isTxt(File file){
        return !isImage(file)
                && !isVideo(file)
                && !isAudio(file);
    }
}
