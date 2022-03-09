package org.ayo.video.support.retriever;

public interface VideoRetrieveCallback {
    void onRetriveFinish(boolean isSuccess, VideoInfo data, Exception e);
}
