package com.zheng.chat.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONString;
import cn.hutool.json.JSONUtil;
import com.zheng.chat.config.Result;
import com.zheng.chat.model.User;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/6 17:44:47
 */
@Slf4j
public class HttpClientUtil {

    private static String postUrl = "http://localhost:8202/productInfo/index/post";
    private static String getUrl = "http://localhost:8202/productInfo/index/get";

    public static void main(String[] args) {
        try {
//            创建客户端对象
            CloseableHttpClient client = HttpClients.createDefault();


            get(client,getUrl);
            post(client,postUrl);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void get(CloseableHttpClient httpClient,String url){
//            连接对象
        try {
//        HttpGet不能携带参数，如果需要参数只能通过拼接
            HttpGet httpGet = new HttpGet(url+"?type=1");

            String execute = httpClient.execute(httpGet, response -> {
                JSONObject entries = new JSONObject(EntityUtils.toString(response.getEntity()));
                Result result = JSONUtil.toBean(entries, Result.class);
                if (result.getCode() == 200) {
                    String data = result.getData().toString();
                    log.info(data);
                    return data;
                } else {
                    log.info(result.getMessage());
                    return result.getMessage();
                }
            });
            log.info("HttpClient的get成功结果："+execute);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(CloseableHttpClient httpClient,String url){

        HttpPost httpPost = new HttpPost(url);
        User user = new User();
        user.setPost(1);
//        添加参数对象
        httpPost.setEntity(new StringEntity(JSONUtil.toJsonStr(user)));
        // 设置请求头
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        try {
            String execute = httpClient.execute(httpPost, response -> {
                JSONObject entries = new JSONObject(EntityUtils.toString(response.getEntity()));
                Result result = JSONUtil.toBean(entries, Result.class);
                if (result.getCode() == 200) {
                    String data = result.getData().toString();
                    log.info(data);
                    return data;
                } else {
                    log.error(result.getMessage());
                    return result.getMessage();
                }
            });
            log.info("HttpClient的post成功结果："+execute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
