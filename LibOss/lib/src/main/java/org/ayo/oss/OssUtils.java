package org.ayo.oss;

import android.os.Environment;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cowthan on 2018/10/27.
 */

public class OssUtils {


    public static boolean debugToOssFile(String ossKey, Object content) {
        String json = (content instanceof String) ? content + "" : JsonUtils.toJsonPretty(content);
        try {
            return OssServer.uploadFileSync("asdsdsdfsdwe", ossKey, json.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean debugToOssFile2(String bucketName, String ossKey, Object content) {
        String json = (content instanceof String) ? content + "" : JsonUtils.toJsonPretty(content);
        try {
            return OssServer.uploadFileSync(bucketName, ossKey, json.getBytes("utf-8"));
        } catch (Exception e) {
            Logs.logCommonError(e, "oss文件上传失败");
        }
        return false;
    }

    public static boolean uploadFileToBucket(String bucketName, String ossKey, File file) {
        boolean isOk = false;
        try {
            File recordDir = new File(Environment.getExternalStorageDirectory(), "1111__wp/oss/");
            recordDir.mkdirs();
            isOk = OssServer.resumableUploadSync(bucketName, ossKey, file.getAbsolutePath(), recordDir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            isOk = false;
        }

        return isOk;
    }

    public static String getCurrentDateForFilename(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String getCurrentDateForFilename2(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
