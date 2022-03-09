package org.ayo.oss;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.language.bm.Lang;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by cowthan on 2018/7/26.
 */

public class OssServer {

    private static OssService ossService;

    private static OssHttpInterface httpInterface;

    public static void setHttpInterface(OssHttpInterface httpInterface) {
        OssServer.httpInterface = httpInterface;
    }

    public static OssService initOSS(String accessId, String accessKey, String endPoint) {
        if (ossService != null) return ossService;
//        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。

        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
//        String stsServer = ((EditText) findViewById(R.id.stsserver)).getText().toString();

        ///STS 鉴权方式，老TM不对
//        if (TextUtils.isEmpty(stsServer)) {
//            credentialProvider = new OSSAuthCredentialsProvider(Config.STSSERVER);
//            ((EditText) findViewById(R.id.stsserver)).setText(Config.STSSERVER);
//        } else {
//            credentialProvider = new OSSAuthCredentialsProvider(stsServer);
//        }
//        credentialProvider = new OSSAuthCredentialsProvider(Config.STSSERVER);


//        String s = "{\n" +
//                "\t\"accessKeyId\":\"STS.NJzGmuVpaogNmWpozzgTEzhd5\",\n" +
//                "\t\"accessKeySecret\":\"AQe6i4EW3Xg7A6GM4RUsmuLzMf2Z3US5po5ATCgkrdaV\",\n" +
//                "\t\"expiration\":\"2019-07-28T04:38:24Z\",\n" +
//                "\t\"securityToken\":\"CAISggJ1q6Ft5B2yfSjIr4nPDNfBu69A2KWlb3HBi3ova9tpla3P1zz2IHxKfHhoAukZsvQ2mm5Y7f8clqJoRoReREvCKMxr9cyTJJZ/yM+T1fau5Jko1beXewHKeSOZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3kkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r/txdaHuFiMzg+46JdM+N6ufMT7MpgyZcguCYro5oEsKPqdihw3wgNR6aJ7gJZD/Tr6pdyHCzFTmU7abbKFqoY2c1MpP/ZkS/Ud8uKcj/p8t6nUjJ/nFKC0vx3Us08agAGd1q6wAz7fSxAkHS6HOSIb6APOhO2Fi9RhBjtXvdqONafKCv5Es1bDqBz6MwP686m/zFVwVubG2pdDZiVdrzlMovHzuVNx449GZmkePNhmGyPQ3u8NfviFFQarbtDqNp6ZRABzJZiaRM5faE4LCoLSdljyMZdFvOH/7PQ0eM+85g==\"\n" +
//                "}";
//        final AssocArray info = JsonUtils.parse(s);
//        Logs.logCommon("oss----" + JsonUtils.toJson(info));

        // 用这种方法new会报这个错： You have no right to access this object because of bucket acl，getFederationToken里的方法好像根本不会调用
//        credentialProvider = new OSSCredentialProvider() {
//
//            @Override
//            public OSSFederationToken getFederationToken() throws ClientException {
//
//                OSSFederationToken auth = new OSSFederationToken(info.getString("accessKeyId", ""),
//                        info.getString("accessKeySecret", ""),
//                        info.getString("securityToken", ""),
//                        info.getString("expiration", ""));
//                return auth;
//            }
//        };

        /// 这个是STS签名，OK
//        credentialProvider = new OSSStsTokenCredentialProvider(info.getString("accessKeyId", ""),
//                info.getString("accessKeySecret", ""),
//                info.getString("securityToken", ""));

//        credentialProvider = new OSSStsTokenCredentialProvider(token.getAccessKeyId(), token.getSignature(), token.getPolicy());


        // 自签名模式
        credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                Lang.file_put_content("/storage/emulated/0/http/signContent.txt", content);
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil

                // 以下是用本地算法进行的演示
//                return "OSS " + AccessKeyId + ":" + base64(hmac-sha1(AccessKeySecret, content));
                String result = "OSS " + "LTAImrMdXHTi1uQ3" + ":" + "nmzddrPbfgiMIs3u1P9ay8WK8DQ=";
                Logs.logCommon("oss--signContent(String content) == " + content);
                Logs.logCommon("oss--signContent(String content) == " + result);

//                try {
//                    return "OSS " + "AccessKeyId" + ":" + Codec.base64.encodeToString(hmacSha1("nE3V6sQcZEl557XvUgeO6XVe9eh6PK", content).getBytes("utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

                String s = null;
                try {
                    s = httpInterface.requestGet("http://192.168.1.3:8080/getPolicy?s=" + URLEncoder.encode(content, "UTF-8"));
                    return "OSS " + "LTAImrMdXHTi1uQ3" + ":" + s;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;

            }
        };

        /// 这个是明文模式，ak和sk直接保存在本地，ak sk鉴权方式，对是对，不安全呢
//        credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAI7gRNHZQQFtVt", "hO3aSkBxe0OpKMgiT4ssf4zYvNT8CG");
        credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessId, accessKey);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(5 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(5 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(1); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(1); // 失败后最大重试次数，默认2次

//        String endpoint = "http://oss-cn-qingdao.aliyuncs.com";
//        String endpoint = "http://oss-cn-beijing.aliyuncs.com";

        OSS oss = null;
        try {
//            oss = new OSSClient(AppCore.app(), "http://" + token.getRegion()  + ".aliyuncs.com", credentialProvider, conf);
            oss = new OSSClient(AppCore.app(), endPoint, credentialProvider, conf);
        } catch (Exception e) {
            Logs.logCommonError(e, "oss初始化失败");
        }

//        OSS oss = new OSSClient(AppCore.app(), endpoint, credentialProvider, conf);
//        OSSLog.enableLog();

        ossService = new OssService(oss, "");
        return ossService;

    }

    public static String hmacSha1(String src, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(src.getBytes("utf-8"));
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void uploadFile(String bucket, String key, String filepath, final BaseOssCallback callback) {
//        addCallback(filepath, callback);
        final Handler handler = new Handler(Looper.getMainLooper());

        ossService.asyncPutImage(bucket, key, filepath, new IOssCallback() {

            @Override
            public void downloadComplete(final InputStream bm) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.downloadComplete(bm);
                    }
                });

            }

            @Override
            public void downloadFail(final String info) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.downloadFail(info);
                    }
                });

            }

            @Override
            public void uploadComplete(final String key) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.uploadComplete(key);

                    }
                });

            }

            @Override
            public void uploadFail(final String key, final String info) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.uploadFail(key, info);

                    }
                });

            }

            @Override
            public void updateProgress(final String key, final int progress) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateProgress(key, progress);

                    }
                });

            }

            @Override
            public void displayInfo(final String info) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.displayInfo(info);

                    }
                });
            }
        });
    }

    public static boolean uploadFileSync(String bucketName, String key, String filepath) {
        return ossService.putImage(bucketName, key, filepath);
    }

    public static boolean uploadFileSync(String key, String filepath) {
        return ossService.putImage(key, filepath);
    }

    public static boolean uploadFileSync(String key, byte[] content) {
        return ossService.putImage(key, content);
    }

    public static boolean uploadFileSync(String bucketName, String key, byte[] content) {
        return ossService.putImage(bucketName, key, content);
    }

    public static boolean resumableUploadSync(String bucketName, String key, String filepath, String recordDir) {
        long fileSize = IOUtil.getFileSize(new File(filepath));
        System.out.println("通话--文件大小：" + fileSize);
        if (fileSize <= 200 * 1024) {
            return uploadFileSync(bucketName, key, filepath);
        }
        return ossService.resumableUpload(bucketName, key, filepath, recordDir);
    }

    public static void addCallback(Object tag, BaseOssCallback callback) {
        OssCallbacks.getDefault().add(tag, callback);
    }

//    public static void removeCallback(Object tag) {
//        OssCallbacks.getDefault().remove(tag);
//    }
//
//    public static final File download(String buck, String ossKey) {
//        File dir = new File(Environment.getExternalStorageDirectory(), "download");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File file = new File(dir, getFilenameFromOssKey(ossKey));
//        if (file.exists()) return file;
//        return ossService.getFile(buck, ossKey, file);
//    }


//    public static String getFilenameFromOssKey(String key) {
//        String[] items = key.split("/");
//        return items[items.length - 1];
//    }
}
