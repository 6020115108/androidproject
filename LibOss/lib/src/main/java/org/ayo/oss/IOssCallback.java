package org.ayo.oss;

import java.io.InputStream;

/**
 * Created by OSS on 2015/12/7 0007.
 * 完成显示图片，上传下载对话框显示，进度条更新等操作。
 */
public interface IOssCallback {


    //下载成功，显示对应的图片
    void downloadComplete(InputStream bm);

    //下载失败，显示对应的失败信息
    void downloadFail(String info) ;

    //上传成功
    void uploadComplete(String key);

    //上传失败，显示对应的失败信息
    void uploadFail(String key, String info);

    //更新进度，取值范围为[0,100]
    void updateProgress(String key, int progress);

    void displayInfo(String info);
}
