package org.ayo.image.compressor.internal;

import android.os.Handler;
import android.os.Looper;

public class InnerThread {
    private static Handler handler = new Handler(Looper.getMainLooper());

    private interface Callback{
        void onFinish(boolean isSuccess, Object result, Throwable e);
    }

    public interface ResultedRunnable{
        Object run();
    }
    public interface MainThreadCallback extends Callback{

    }

    public interface CurrentThreadCallback extends Callback{

    }

    public static void runInThread(final ResultedRunnable runnable, final MainThreadCallback callback){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Throwable e2 = null;
                Object result = null;
                try {
                    result = runnable.run();
                }catch (Throwable e){
                    e2 = e;
                }

                final Throwable ex = e2;
                final Object result2 = result;

                if(callback instanceof MainThreadCallback){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callback != null) callback.onFinish(ex == null, result2, ex);
                        }
                    });
                }else if(callback instanceof CurrentThreadCallback){
                    if(callback != null) callback.onFinish(ex == null, result, ex);
                }
            }
        });
        thread.setDaemon(false);
        thread.setName(System.currentTimeMillis() + "");
        thread.setPriority(1);
        thread.start();
    }

}
