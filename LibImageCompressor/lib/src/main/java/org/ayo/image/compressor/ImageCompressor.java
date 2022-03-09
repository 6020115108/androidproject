package org.ayo.image.compressor;

import android.content.Context;

import org.ayo.image.compressor.biscuit.Biscuit;
import org.ayo.image.compressor.biscuit.CompressException;
import org.ayo.image.compressor.biscuit.CompressListener;
import org.ayo.image.compressor.internal.InnerThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageCompressor {

    public interface OnCompressCallback{
        void onCompressFinish(List<CompressResult> results);
    }

    public static void compress(final Context context, final List<String> paths, File outDir, final OnCompressCallback compressCallback){
        final String outDirPath = outDir.getAbsolutePath().endsWith("/")
                ? outDir.getAbsolutePath()
                : outDir.getAbsolutePath()+"/";
        if(!outDir.exists()) outDir.mkdirs();

        final List<CompressResult> paths2 = new ArrayList<>();
        InnerThread.runInThread(new InnerThread.ResultedRunnable() {
            @Override
            public Object run() {
                int count = paths == null ? 0 : paths.size();

                /** 不使用传入list的方式，因为回调里如果是error，不知道是哪张出错啊，所以只能咋这么一张一张的来 */
                for (int i = 0; i < count; i++) {
                    final String path = paths.get(i);
                    Biscuit mBiscuit =
                            Biscuit.with(context)
                                    .path(path) //可以传入一张图片路径，也可以传入一个图片路径列表
                                    .loggingEnabled(true)//是否输出log 默认输出
//                        .quality(50)//质量压缩值（0...100）默认已经非常接近微信，所以没特殊需求可以不用自定义
                                    .originalName(true) //使用原图名字来命名压缩后的图片，默认不使用原图名字,随机图片名字
//                                        .listener(mCompressListener)//压缩监听
//                                        .listener(mOnCompressCompletedListener)//压缩完成监听
                                    .targetDir(outDirPath)//自定义压缩保存路径
//                        .executor(executor) //自定义实现执行，注意：必须在子线程中执行 默认使用AsyncTask线程池执行
//                        .ignoreAlpha(true)//忽略alpha通道，对图片没有透明度要求可以这么做，默认不忽略。
//                        .compressType(Biscuit.SAMPLE)//采用采样率压缩方式，默认是使用缩放压缩方式，也就是和微信的一样。
                                    .ignoreLessThan(100)//忽略小于100kb的图片不压缩，返回原图路径
                                    .build();

                    CompressListener cl = new CompressListener() {
                        @Override
                        public void onSuccess(String compressedPath) {
                            CompressResult r = new CompressResult();
                            r.isSuccess = true;
                            r.originalPath = path;
                            r.compressedPath = compressedPath;
                            paths2.add(r);
                        }

                        @Override
                        public void onError(CompressException e) {
                            CompressResult r = new CompressResult();
                            r.isSuccess = false;
                            r.originalPath = path;
                            r.error = new RuntimeException(e);
                            paths2.add(r);
                        }
                    };
                    mBiscuit.addListener(cl);
                    List<String> result = mBiscuit.syncCompress();
                    mBiscuit.removeListener(cl);
                }
                return null;
            }
        }, new InnerThread.MainThreadCallback() {
            @Override
            public void onFinish(boolean isSuccess, Object result, Throwable e) {
                compressCallback.onCompressFinish(paths2);
            }
        });
    }

}
