package org.ayo.oss;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;


import org.apache.commons.codec.language.bm.Lang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by mOss on 2015/12/7 0007.
 * 支持普通上传，普通下载
 */
public class OssService {

    public OSS mOss;
    private String mBucket;
    private String mCallbackAddress;
    private static String mResumableObjectKey = "resumableObject";

    public OssService(OSS oss, String bucket) {
        this.mOss = oss;
        this.mBucket = bucket;
    }

//    public void setBucketName(String bucket) {
//        this.mBucket = bucket;
//    }
//
//    public void initOss(OSS _oss) {
//        this.mOss = _oss;
//    }
//
//    public void setCallbackAddress(String callbackAddress) {
//        this.mCallbackAddress = callbackAddress;
//    }

//    public void asyncGetImage(final String object, final IOssCallback callback) {
//
//        final long get_start = System.currentTimeMillis();
//        if ((object == null) || object.equals("")) {
//            Log.w("AsyncGetImage", "ObjectNull");
//            return;
//        }
//
//        GetObjectRequest get = new GetObjectRequest(mBucket, object);
//        get.setCRC64(OSSRequest.CRC64Config.YES);
//        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
//            @Override
//            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
//                Log.d("GetObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                callback.updateProgress(object, progress);
//                callback.displayInfo("下载进度: " + String.valueOf(progress) + "%");
//            }
//        });
//        OSSAsyncTask task = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
//            @Override
//            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
//                // 请求成功
//                InputStream inputStream = result.getObjectContent();
//                //Bitmap bm = BitmapFactory.decodeStream(inputStream);
//                try {
//                    //需要根据对应的View大小来自适应缩放
//
////                    Bitmap bm = mDisplayer.autoResizeFromStream(inputStream);
//                    long get_end = System.currentTimeMillis();
//                    OSSLog.logDebug("get cost: " + (get_end - get_start) / 1000f);
//                    callback.downloadComplete(inputStream);
//                    callback.displayInfo("Bucket: " + mBucket + "\nObject: " + request.getObjectKey() + "\nRequestId: " + result.getRequestId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        inputStream.close();
//                    } catch (Throwable e) {
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                String info = "";
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                    info = clientExcepion.toString();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                    info = serviceException.toString();
//                }
//                callback.downloadFail(info);
//                callback.displayInfo(info);
//            }
//        });
//    }


    public void asyncPutImage(String bucket, final String object, String localFile, final IOssCallback callback) {
        final long upload_start = System.currentTimeMillis();

        if (object.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, object, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);
                callback.updateProgress(object, progress);
                callback.displayInfo("上传进度: " + String.valueOf(progress) + "%");
            }
        });

        OSSAsyncTask task = mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {


            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                long upload_end = System.currentTimeMillis();
                OSSLog.logDebug("upload cost: " + (upload_end - upload_start) / 1000f);
                callback.uploadComplete(object);
                callback.displayInfo("Bucket: " + mBucket
                        + "\nObject: " + request.getObjectKey()
                        + "\nETag: " + result.getETag()
                        + "\nRequestId: " + result.getRequestId()
                        + "\nCallback: " + result.getServerCallbackReturnBody());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
                callback.uploadFail(object, info);
                callback.displayInfo(info);
            }
        });
    }

    public boolean resumableUpload(String bucketName, final String object, String localFile, String recordDirPath) {

        if (object.equals("")) {
            Log.w("通话", "ObjectNull");
            return false;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("通话", "FileNotExist");
            Log.w("通话", localFile);
            return false;
        }

        String recordDirectory = recordDirPath;
        if (Lang.isEmpty(recordDirectory)) {
            recordDirectory = new File(AppCore.wordDir(), "oss_resume").getAbsolutePath();
        }
        File recordDir = new File(recordDirectory);
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // 构造上传请求
        ResumableUploadRequest put = new ResumableUploadRequest(bucketName, object, localFile, recordDirectory);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                callback.updateProgress(object, progress);
//                callback.displayInfo("上传进度: " + String.valueOf(progress) + "%");
//            }
//        });

        try {
            ResumableUploadResult task = mOss.resumableUpload(put);
            System.out.println("1111122233-1--" + task.getStatusCode() + "----" + object);
            System.out.println("1111122233-2--" + JsonUtils.toJson(task));
            System.out.println("1111122233-3--" + "----------------------------");
            return task.getStatusCode() == 200;
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("1111122233-4--" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("1111122233-5--" + e.getMessage());
        }
        return false;
    }

    public boolean putImage(final String object, String localFile) {
        return putImage(mBucket, object, localFile);
    }

    public boolean putImage(String bucketName, final String object, String localFile) {
        final long upload_start = System.currentTimeMillis();

        if (object.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return false;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return false;
        }

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, object, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                callback.updateProgress(object, progress);
//                callback.displayInfo("上传进度: " + String.valueOf(progress) + "%");
//            }
//        });

        try {
            PutObjectResult task = mOss.putObject(put);
            System.out.println("1111122233-1--" + task.getStatusCode() + "----" + object);
            System.out.println("1111122233-2--" + JsonUtils.toJson(task));
            System.out.println("1111122233-3--" + "----------------------------");
            return task.getStatusCode() == 200;
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("1111122233-4--" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("The OSS Access Key Id you provided does not exist in our records")){
                System.out.println("1111122233-6--" + "token过期");
            }
            System.out.println("1111122233-5--" + e.getMessage());
        }
        return false;
    }

    public boolean putImage(final String object, byte[] content) {

        return putImage(mBucket, object, content);
    }


    public boolean putImage(String bucketName, final String object, byte[] content) {

        if (object.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return false;
        }

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, object, content);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                callback.updateProgress(object, progress);
//                callback.displayInfo("上传进度: " + String.valueOf(progress) + "%");
//            }
//        });

        try {
            PutObjectResult task = mOss.putObject(put);
            System.out.println("1111122233-1--" + task.getStatusCode() + "----" + object);
            System.out.println("1111122233-2--" + JsonUtils.toJson(task));
            System.out.println("1111122233-3--" + "----------------------------");
            return task.getStatusCode() == 200;
        } catch (ClientException e) {
            System.out.println("1111122233-4--" + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("1111122233-5--" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Downloads the files with specified prefix in the asynchronous way.
//    public void asyncListObjectsWithBucketName() {
//        ListObjectsRequest listObjects = new ListObjectsRequest(mBucket);
//        // Sets the prefix
//        listObjects.setPrefix("android");
//        listObjects.setDelimiter("/");
//        // Sets the success and failure callback. calls the Async API
//        OSSAsyncTask task = mOss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
//            @Override
//            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
//                String info = "";
//                OSSLog.logDebug("AyncListObjects", "Success!");
//                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
//                    info += "\n" + String.format("object: %s %s %s", result.getObjectSummaries().get(i).getKey(), result.getObjectSummaries().get(i).getETag(), result.getObjectSummaries().get(i).getLastModified().toString());
//                    OSSLog.logDebug("AyncListObjects", info);
//                }
//                callback.displayInfo(info);
//            }
//
//            @Override
//            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // request exception
//                if (clientExcepion != null) {
//                    // client side exception such as network exception
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // service side exception.
//                    OSSLog.logError("ErrorCode", serviceException.getErrorCode());
//                    OSSLog.logError("RequestId", serviceException.getRequestId());
//                    OSSLog.logError("HostId", serviceException.getHostId());
//                    OSSLog.logError("RawMessage", serviceException.getRawMessage());
//                }
//                callback.downloadFail("Failed!");
//                callback.displayInfo(serviceException.toString());
//            }
//        });
//    }

    // Gets file's metadata
//    public void headObject(String objectKey) {
//        // Creates a request to get the file's metadata
//        HeadObjectRequest head = new HeadObjectRequest(mBucket, objectKey);
//
//        OSSAsyncTask task = mOss.asyncHeadObject(head, new OSSCompletedCallback<HeadObjectRequest, HeadObjectResult>() {
//            @Override
//            public void onSuccess(HeadObjectRequest request, HeadObjectResult result) {
//                OSSLog.logDebug("headObject", "object Size: " + result.getMetadata().getContentLength());
//                OSSLog.logDebug("headObject", "object Content Type: " + result.getMetadata().getContentType());
//
//                callback.displayInfo(result.toString());
//            }
//
//            @Override
//            public void onFailure(HeadObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // request exception
//                if (clientExcepion != null) {
//                    // client side exception,  such as network exception
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // service side exception
//                    OSSLog.logError("ErrorCode", serviceException.getErrorCode());
//                    OSSLog.logError("RequestId", serviceException.getRequestId());
//                    OSSLog.logError("HostId", serviceException.getHostId());
//                    OSSLog.logError("RawMessage", serviceException.getRawMessage());
//                }
//                callback.downloadFail("Failed!");
//                callback.displayInfo(serviceException.toString());
//            }
//        });
//    }

//    public void asyncMultipartUpload(final String uploadKey, String uploadFilePath) {
//        MultipartUploadRequest request = new MultipartUploadRequest(mBucket, uploadKey,
//                uploadFilePath);
//        request.setCRC64(OSSRequest.CRC64Config.YES);
//        request.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
//
//            @Override
//            public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
//                OSSLog.logDebug("[testMultipartUpload] - " + currentSize + " " + totalSize, false);
//            }
//        });
//        mOss.asyncMultipartUpload(request, new OSSCompletedCallback<MultipartUploadRequest, CompleteMultipartUploadResult>() {
//            @Override
//            public void onSuccess(MultipartUploadRequest request, CompleteMultipartUploadResult result) {
//                callback.uploadComplete(uploadKey);
//                callback.displayInfo(request.toString());
//            }
//
//            @Override
//            public void onFailure(MultipartUploadRequest request, ClientException clientException, ServiceException serviceException) {
//                if (clientException != null) {
//                    callback.displayInfo(clientException.toString());
//                } else if (serviceException != null) {
//                    callback.displayInfo(serviceException.toString());
//                }
//
//            }
//        });
//    }

//    void asyncResumableUpload(final String resumableFilePath) {
//        ResumableUploadRequest request = new ResumableUploadRequest(mBucket, mResumableObjectKey, resumableFilePath);
//        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
//            @Override
//            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
//                Log.d("GetObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                callback.updateProgress(resumableFilePath, progress);
//                callback.displayInfo("上传进度: " + String.valueOf(progress) + "%");
//            }
//        });
//        OSSAsyncTask task = mOss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
//            @Override
//            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
//                callback.uploadComplete(resumableFilePath);
//                callback.displayInfo(request.toString());
//            }
//
//            @Override
//            public void onFailure(ResumableUploadRequest request, ClientException clientException, ServiceException serviceException) {
//                if (clientException != null) {
//                    callback.displayInfo(clientException.toString());
//                } else if (serviceException != null) {
//                    callback.displayInfo(serviceException.toString());
//                }
//            }
//        });
//    }

    // If the bucket is private, the signed URL is required for the access.
    // Expiration time is specified in the signed URL.
//    public void presignConstrainedURL() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Gets the signed url, the expiration time is 5 minute
//                    String url = mOss.presignConstrainedObjectURL(mBucket, "androidTest.jpeg", 5 * 60);
//                    OSSLog.logDebug("signContrainedURL", "get url: " + url);
//                    // 访问该url
//                    Request request = new Request.Builder().url(url).build();
//                    Response resp = null;
//
//                    resp = new OkHttpClient().newCall(request).execute();
//
//                    if (resp.code() == 200) {
//                        OSSLog.logDebug("signContrainedURL", "object size: " + resp.body().contentLength());
//                        callback.displayInfo(resp.toString());
//                    } else {
//                        OSSLog.logDebug("signContrainedURL", "get object failed, error code: " + resp.code()
//                                + "error message: " + resp.message());
//                        callback.displayInfo(resp.toString());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    callback.displayInfo(e.toString());
//                } catch (ClientException e) {
//                    e.printStackTrace();
//                    callback.displayInfo(e.toString());
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * Delete a non-empty bucket.
//     * Create a bucket, and add files into it.
//     * Try to delete the bucket and failure is expected.
//     * Then delete file and then delete bucket
//     */
//    public void deleteNotEmptyBucket(final String bucket, final String filePath) {
//        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
//        // 创建bucket
//        try {
//            mOss.createBucket(createBucketRequest);
//        } catch (ClientException clientException) {
//            clientException.printStackTrace();
//            callback.displayInfo(clientException.toString());
//        } catch (ServiceException serviceException) {
//            serviceException.printStackTrace();
//            callback.displayInfo(serviceException.toString());
//        }
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, "test-file", filePath);
//        try {
//            mOss.putObject(putObjectRequest);
//        } catch (ClientException clientException) {
//            clientException.printStackTrace();
//        } catch (ServiceException serviceException) {
//            serviceException.printStackTrace();
//        }
//        final DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucket);
//        OSSAsyncTask deleteBucketTask = mOss.asyncDeleteBucket(deleteBucketRequest, new OSSCompletedCallback<DeleteBucketRequest, DeleteBucketResult>() {
//            @Override
//            public void onSuccess(DeleteBucketRequest request, DeleteBucketResult result) {
//                OSSLog.logDebug("DeleteBucket", "Success!");
//                callback.displayInfo(result.toString());
//            }
//
//            @Override
//            public void onFailure(DeleteBucketRequest request, ClientException clientException, ServiceException serviceException) {
//                // request exception
//                if (clientException != null) {
//                    // client side exception,  such as network exception
//                    clientException.printStackTrace();
//                    callback.displayInfo(clientException.toString());
//                }
//                if (serviceException != null) {
//                    // The bucket to delete is not empty.
//                    if (serviceException.getStatusCode() == 409) {
//                        // Delete files
//                        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "test-file");
//                        try {
//                            mOss.deleteObject(deleteObjectRequest);
//                        } catch (ClientException clientexception) {
//                            clientexception.printStackTrace();
//                        } catch (ServiceException serviceexception) {
//                            serviceexception.printStackTrace();
//                        }
//                        // Delete bucket again
//                        DeleteBucketRequest deleteBucketRequest1 = new DeleteBucketRequest(bucket);
//                        try {
//                            mOss.deleteBucket(deleteBucketRequest1);
//                        } catch (ClientException clientexception) {
//                            clientexception.printStackTrace();
//                            callback.displayInfo(clientexception.toString());
//                            return;
//                        } catch (ServiceException serviceexception) {
//                            serviceexception.printStackTrace();
//                            callback.displayInfo(serviceexception.toString());
//                            return;
//                        }
//                        OSSLog.logDebug("DeleteBucket", "Success!");
//                        callback.displayInfo("The Operation of Deleting Bucket is successed!");
//                    }
//                }
//            }
//        });
//    }
//
//
//
//
//    public void imagePersist(String fromBucket, String fromObjectKey, String toBucket, String toObjectkey, String action) {
//
//        ImagePersistRequest request = new ImagePersistRequest(fromBucket, fromObjectKey, toBucket, toObjectkey, action);
//
//        OSSAsyncTask task = mOss.asyncImagePersist(request, new OSSCompletedCallback<ImagePersistRequest, ImagePersistResult>() {
//            @Override
//            public void onSuccess(ImagePersistRequest request, ImagePersistResult result) {
////                callback.displayInfo(result.getServerCallbackReturnBody());
//            }
//
//            @Override
//            public void onFailure(ImagePersistRequest request, ClientException clientException, ServiceException serviceException) {
//                if (clientException != null) {
//                    callback.displayInfo(clientException.toString());
//                } else if (serviceException != null) {
//                    callback.displayInfo(serviceException.toString());
//                }
//            }
//        });
//    }

    public File getFile(String buck, String ossKey, File file) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            GetObjectResult r = mOss.getObject(new GetObjectRequest(buck, ossKey));
            if (null == r) {
                return null;
            }
            in = r.getObjectContent();
            out = new FileOutputStream(file);
            IOUtil.copy(in, out);
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);
        }
    }


}
