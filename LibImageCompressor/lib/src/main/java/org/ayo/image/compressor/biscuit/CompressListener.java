package org.ayo.image.compressor.biscuit;

/**
 * Created by seek on 2017/6/24.
 */

public interface CompressListener {

    void onSuccess(String compressedPath);

    void onError(CompressException e);
}
