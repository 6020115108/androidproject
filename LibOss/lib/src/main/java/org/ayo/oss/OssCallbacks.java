package org.ayo.oss;



/**
 * Created by cowthan on 2018/7/26.
 */

public class OssCallbacks extends MyLifeCircleRepository<BaseOssCallback> {

    private OssCallbacks(){}

    private static final class Holder{
        private static final OssCallbacks instance = new OssCallbacks();
    }

    public static OssCallbacks getDefault(){
        return Holder.instance;
    }
}
