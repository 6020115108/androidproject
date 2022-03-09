package org.ayo.nano.sev;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ayo.nano.InnerUtils;
import org.ayo.nano.LibNano;
import org.ayo.nano.R;

import java.io.IOException;
import java.util.List;

public class HttpMainActivity extends Activity {
    

    private TextView mTipsTextView;
    private VideoServer mVideoServer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_ac_main);
        mTipsTextView = (TextView)findViewById(R.id.TipsTextView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission_group.STORAGE}, 10);
        }else{
            start();
        }


    }
    private void start(){
        mVideoServer = new VideoServer(VideoServer.DEFAULT_SERVER_PORT);
        mTipsTextView.setText("请在远程浏览器中输入:\n\n"+getLocalIpStr(LibNano.app)+":"+VideoServer.DEFAULT_SERVER_PORT);
        try {
            mVideoServer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
            mTipsTextView.setText(e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        start();
    }

    @Override
    protected void onDestroy() {
//        mVideoServer.stop();
        super.onDestroy();
    }

    public static String getLocalIpStr(Context context) {        
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIpAddr(wifiInfo.getIpAddress());
    }
    
    private static String intToIpAddr(int ip) {
        return (ip & 0xff) + "." + ((ip>>8)&0xff) + "." + ((ip>>16)&0xff) + "." + ((ip>>24)&0xff);
    }
    
}
