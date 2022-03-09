package org.ayo.nano.sev;

import android.os.Environment;
import android.util.Log;

import org.ayo.nano.InnerUtils;
import org.ayo.nano.LibNano;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class VideoServer extends NanoHTTPD {

    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final String TAG = VideoServer.class.getSimpleName();

    private static final String REQUEST_ROOT = "/";

//    private String mVideoFilePath;
//    private int mVideoWidth = 0;
//    private int mVideoHeight = 0;

    public VideoServer(int port) {
        super(port);
//        mVideoFilePath = filepath;
//        mVideoWidth = width;
//        mVideoHeight = height;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d(TAG, "OnRequest: " + session.getUri()); //    /dsfa  == ?http://192.168.1.5:8080/dsfa?x=23&dd=22
        Log.d(TAG, "OnRequest: " + session.getQueryParameterString()); //   x=23&dd=22
        Map<String, String> params = Utils.parseQueryString(session.getQueryParameterString());
        Log.d(TAG, "OnRequest: pairs = " + JsonUtils.toJson(params)); //   x=23&dd=22



        if (REQUEST_ROOT.equals(session.getUri())) {
            return responseHtml(session, "Hello");
        } else if("/favicon.ico".equals(session.getUri())){
            return response404(session, session.getUri());
        }else if ("/files".equals(session.getUri())) {
            // http://192.168.1.5:8080/files?path=xxx

            String path = params.get("path");
            if (InnerUtils.isEmpty(path)) {
                //path = "/storage/emulated/0";
                // 什么都不传，则列出内外目录的根路径

                StringBuilder body = new StringBuilder();
                String tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                String filePath = LibNano.app.getFilesDir().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "getFilesDir() --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = LibNano.app.getCacheDir().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "getCacheDir() --- " + filePath);
                body.append(tmpl);
                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = LibNano.app.getDatabasePath("aa.db").getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "getDatabasePath(null) --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = LibNano.app.getExternalFilesDir(null).getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "getExternalFilesDir(null) --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = LibNano.app.getExternalCacheDir().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "getExternalCacheDir() --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = Environment.getRootDirectory().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "Environment.getRootDirectory() --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = Environment.getDataDirectory().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "Environment.getDataDirectory() --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "Environment.getDownloadCacheDirectory() --- " + filePath);
                body.append(tmpl);

                tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                tmpl = tmpl.replace("{href}", "./files?path=" + filePath);
                tmpl = tmpl.replace("{path}", "Environment.getExternalStorageDirectory() --- " + filePath);
                body.append(tmpl);


                try {
                    String html = InnerUtils.readStr(LibNano.app.getAssets().open("files.html"));
                    html = html.replace(" {{$body}}", body.toString());
                    return responseHtml(session, html);
                } catch (IOException e) {
                    e.printStackTrace();
                    return response404(session, e.getMessage());
                }

            }else{
                Log.d(TAG, "OnRequest: path=" + path); //   x=23&dd=22

                File file = new File(path);
                if (!file.exists()) {
                    return response404(session, path);
                } else if (file.isDirectory()) {
                    // 目录
                    File[] files = file.listFiles();
                    if(files == null) files=new File[0];
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            int type1 = f1.isDirectory() ? -1 : 1;
                            int type2 = f2.isDirectory() ? -1 : 1;
                            if(type1 != type2){
//                                return (type1 - type2 < 0) ? -1 : 0;
                                return (type1 - type2);
                            }else{
                                return f1.getName().compareTo(f2.getName());
                            }
                        }
                    });

                    StringBuilder body = new StringBuilder();
                    for (int i = 0; i < InnerUtils.count(files); i++) {
                        // <li><a href="{href}">{path}</a></li>
                        String tmpl = "<li><a href=\"{href}\">{path}</a></li>";
                        tmpl = tmpl.replace("{href}", "./files?path=" + files[i].getAbsolutePath());
                        tmpl = tmpl.replace("{path}", files[i].getAbsolutePath());
                        body.append(tmpl);
                    }
                    try {
                        String html = InnerUtils.readStr(LibNano.app.getAssets().open("files.html"));
                        html = html.replace(" {{$body}}", body.toString());
                        return responseHtml(session, html);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return response404(session, e.getMessage());
                    }

                } else {
                    // 文件
                    if(Utils.isVideo(file)){
                        return responseVidoHtml(session, path);
                    }else if(Utils.isAudio(file)){
                        return responseAudioHtml(session, path);
                    }else if(Utils.isImage(file)){
                        return responseImageHtml(session, path);
                    }else if(file.getAbsolutePath().endsWith(".db")){
                        List<String> tables = DbReader.listTables(file.getAbsolutePath());
                        Map<String, List<AssocArray2>> datas = new HashMap<>();
                        for (int i = 0; i < InnerUtils.count(tables); i++) {
                            datas.put(tables.get(i), DbReader.listRows(file.getAbsolutePath(), tables.get(i)));
                        }
                        return responseHtml(session, JsonUtils.toJsonPretty(datas));
                    }else{
                        try {
                            return responseTxtFileHtml(session, path);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return response404(session, path);
                        }
                    }
                }
            }



        }else if ("/stream".equals(session.getUri())) {
            String path = params.get("path");
            File file = new File(path);

            if (InnerUtils.isEmpty(path)) {
                return response404(session, path);
            }else{
                if(Utils.isVideo(file)){
                    return responseVido(session, path);
                }else if(Utils.isAudio(file)){
                    return responseAudio(session, path);
                }else if(Utils.isImage(file)){
                    return responseImage(session, path);
                }else{
                    return response404(session, path);
                }
            }
        }else{
            return response404(session, session.getUri());
        }
    }

    public Response responseVidoHtml(IHTTPSession session, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return response404(session, path);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><meta charset='utf-8' /></head><body>");
        builder.append("<video ");
        builder.append("width='" + 320 + "' ");
        builder.append("height='" + 320 + "' ");
        builder.append("controls>");
        builder.append("<source src='./stream?path=" + path + "' ");
        builder.append("type=" + getQuotaStr("video/mp4") + ">");
        builder.append("Your browser doestn't support HTML5");
        builder.append("</video>");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }

    public Response responseAudioHtml(IHTTPSession session, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return response404(session, path);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>aa</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<audio controls>\n" +
                "  <source src=\"{path}\" type=\"audio/mpeg\">\n" +
                "您的浏览器不支持 audio 元素。\n" +
                "</audio>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        String s = builder.toString().replace("{path}", "./stream?path=" + path);
        return new Response(s);
    }
  public Response responseImageHtml(IHTTPSession session, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return response404(session, path);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><meta charset='utf-8' /></head><body>");
        builder.append("<img src='{path}'></img>");
        builder.append("</body></html>\n");
        String s = builder.toString().replace("{path}", "./stream?path=" + path);
        return new Response(s);
    }

    public Response responseTxtFileHtml(IHTTPSession session, String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return response404(session, path);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><meta charset='utf-8' /></head><body>");
        builder.append("<pre>{body}</pre>");
        builder.append("</body></html>\n");
        String s = builder.toString().replace("{body}", InnerUtils.readStr(new FileInputStream(path)));
        return new Response(s);
    }

    public Response responseHtml(IHTTPSession session, String html) {
        return new Response(html);
    }

    public Response responseVido(IHTTPSession session, String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return new Response(Status.OK, "video/*", fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return response404(session, path);
        }
    }

    public Response responseAudio(IHTTPSession session, String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return new Response(Status.OK, "audio/*", fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return response404(session, path);
        }
    }

    public Response responseImage(IHTTPSession session, String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return new Response(Status.OK, "image/*", fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return response404(session, path);
        }
    }

    public Response response404(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found " + url + " !");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }


    protected String getQuotaStr(String text) {
        return "\"" + text + "\"";
    }

}
