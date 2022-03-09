package com.xd.xoid.nano;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.xd.xoid.nano.sev.VideoServer;

import java.io.IOException;

public class LibNano {

    public static Application app;

    private static VideoServer mVideoServer;


    public static void start(Application app, int port) {
        LibNano.app = app;
        mVideoServer = new VideoServer(port);
//        mTipsTextView.setText("请在远程浏览器中输入:\n\n"+getLocalIpStr(LibNano.app)+":"+VideoServer.DEFAULT_SERVER_PORT);
        try {
            mVideoServer.start();
            Log.w("nano", "手机端http服务器地址：http://" + getLocalIpStr(LibNano.app) + "/" + port + "/files");

        } catch (IOException e) {
            e.printStackTrace();
            Log.w("nano", "启动错误：" + e.getMessage());

        }
    }

    private static String getLocalIpStr(Context context) {
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIpAddr(wifiInfo.getIpAddress());
    }

    private static String intToIpAddr(int ip) {
        return (ip & 0xff) + "." + ((ip>>8)&0xff) + "." + ((ip>>16)&0xff) + "." + ((ip>>24)&0xff);
    }

}
