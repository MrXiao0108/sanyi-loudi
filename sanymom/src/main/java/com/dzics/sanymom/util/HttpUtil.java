package com.dzics.sanymom.util;


import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2019/10/29.
 */
@Slf4j
public class HttpUtil {
    /**
     * 发出Http的get请求
     *
     * @Param url 请求的URL地址
     */
    public static String sendHttpGetRequest(String url) {
        log.info("发送外部请求url:" + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException ex) {
            throw new RuntimeException("URL:" + url + ";ErrorMsg: ", ex);
        }
    }

    /**
     * 发出Http的Post请求
     *
     * @param url  目标的URL
     * @param json 请求体
     */
    public static String sendHttpPostRequest(String url, String json) {
        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            //通过JSON格式构建Post的请求体
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Throwable ex) {
            throw new RuntimeException("URL:" + url + ";ErrorMsg: " + ex.getMessage());
        }
    }

    public static String sendHttpPostRequestHeader(String url, String json, Map<String, String> header) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        //通过JSON格式构建Post的请求体
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> stringStringEntry : header.entrySet()) {
            builder.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        Request build = builder.post(body).build();
        try {
            Response response = client.newCall(build).execute();
            return response.body().string();
        } catch (IOException ex) {
            throw new RuntimeException("URL:" + url + ";ErrorMsg: " + ex.getMessage());
        }
    }

    // 请求方法
    public static String httpsRequest(String requestUrl, String requestMethod,
                                      String outputStr) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type",
                    "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            log.warn("连接超时：{}" + ce);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
        } catch (Exception e) {
            log.error("https请求异常：{}" + e);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }

    }

}
