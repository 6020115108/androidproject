package org.ayo.video.support.retriever;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class VideoRetriever {

    public static void retrieveVideoAsync(final Context context, final String mUri, final VideoRetrieveCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final VideoInfo v = retrieveVideoSync(context, mUri);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callback != null) callback.onRetriveFinish(true, v, null);
                        }
                    });

                }catch (final Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callback != null) callback.onRetriveFinish(false, null, e);
                        }
                    });

                }

            }
        }).start();
    }

    public static VideoInfo retrieveVideoSync(Context context, String mUri) {
        if (mUri == null || "".equals(mUri)) return null;
        VideoInfo v = new VideoInfo();
        v.path = mUri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri.startsWith("http")) {
                mmr.setDataSource(mUri, new HashMap<String, String>());
            } else {
                mmr.setDataSource(mUri);
            }
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
            String rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
            String frameCount = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT); // 帧数
            Bitmap bitmap = mmr.getFrameAtTime(1 * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            v.path = mUri;
            v.duration = (int) (toLong(duration) / 1000);
            v.width = toInt(width);
            v.height = toInt(height);
            v.rotation = toInt(rotation);
            v.frameCount = toInt(frameCount);
            File dir = new File(context.getExternalFilesDir(null), "video");
            if(!dir.exists()) dir.mkdirs();
            File thumbFile = new File(dir, "thumb_" + System.currentTimeMillis() + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(thumbFile));
            v.thumbPath = thumbFile.getAbsolutePath();
//            Log.i("视频retrive", JsonUtils.toJson(v));
            return v;
        } catch (Exception ex) {
//            Log.e("视频retrive", "视频retrive出错", ex);
            throw new RuntimeException(ex);
        } finally {
            mmr.release();
        }
    }

    public static void retrieveAudioAsync(final Context context, final String mUri, final VideoRetrieveCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final VideoInfo v = retrieveAudioSync(context, mUri);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callback != null) callback.onRetriveFinish(true, v, null);
                        }
                    });

                }catch (final Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callback != null) callback.onRetriveFinish(false, null, e);
                        }
                    });

                }

            }
        }).start();
    }

    public static VideoInfo retrieveAudioSync(Context context, String mUri) {
        if (mUri == null || "".equals(mUri)) return null;
        VideoInfo v = new VideoInfo();
        v.path = mUri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri.startsWith("http")) {
                mmr.setDataSource(mUri, new HashMap<String, String>());
            } else {
                mmr.setDataSource(mUri);
            }
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            v.path = mUri;
            v.duration = (int) (toLong(duration) / 1000);
//            Log.i("视频retrive", JsonUtils.toJson(v));
            return v;
        } catch (Exception ex) {
            Log.e("视频retrive", "视频retrive出错", ex);
            throw new RuntimeException(ex);
        } finally {
            mmr.release();
        }
    }

    private static int toInt(String strInt) {
        try {
            return Integer.parseInt(strInt);
        } catch (Exception e) {
            return 0;
        }
    }

    private static long toLong(String strInt) {
        try {
            return Long.parseLong(strInt);
        } catch (Exception e) {
            return 0;
        }
    }
}
