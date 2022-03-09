package org.ayo.oss;

import java.io.File;

public interface OssHttpInterface {

    File download(String url, String path);

    String requestGet(String url);
}
