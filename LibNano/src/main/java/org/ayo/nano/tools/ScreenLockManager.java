package org.ayo.nano.tools;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import org.ayo.nano.LibNano;


/**
 * Created by qiaoliang on 2017/6/8.
 * 屏幕点亮，关闭，解锁，锁屏
 *
 * 需要权限：
 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
 */

public class ScreenLockManager {

    private static final class H{
        private static final ScreenLockManager instance = new ScreenLockManager();
    }

    public static ScreenLockManager getDefault(){
        return H.instance;
    }

    private ScreenLockManager(){

    }

    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private KeyguardManager keyguardManager;
    KeyguardManager.KeyguardLock kl;

    /**
     * 点亮屏幕，但不解锁
     */
    public void turnScreenOn(){
        //获取电源管理器对象
        pm = (PowerManager) LibNano.app.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
//        keyguardManager = (KeyguardManager)App.app.getSystemService(Context.KEYGUARD_SERVICE);
//        kl = keyguardManager.newKeyguardLock("unLock");
        wl.acquire();
        wl.release();
    }

    public static void turnScreenOff(){
//        wl.acquire();
//        wl.release();
    }


    public void lockScreen(){
        //获取电源管理器对象
        pm = (PowerManager) LibNano.app.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        keyguardManager = (KeyguardManager)LibNano.app.getSystemService(Context.KEYGUARD_SERVICE);
        kl = keyguardManager.newKeyguardLock("unLock");
        wl.acquire();

        //重新启用自动加锁
        kl.reenableKeyguard();
        //释放
        wl.release();
    }

    public void unlockScreen(){
        //获取电源管理器对象
        pm = (PowerManager) LibNano.app.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        keyguardManager = (KeyguardManager)LibNano.app.getSystemService(Context.KEYGUARD_SERVICE);
        kl = keyguardManager.newKeyguardLock("unLock");
        wl.acquire();

        //解锁
        kl.disableKeyguard();

        wl.release();
    }

}
