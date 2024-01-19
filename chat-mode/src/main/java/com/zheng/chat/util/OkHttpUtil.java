package com.zheng.chat.util;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.zheng.chat.config.Result;
import com.zheng.chat.model.User;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/6 22:44:08
 */
@Slf4j
public class OkHttpUtil {

    private static String postUrl = "http://localhost:8202/productInfo/index/post";
    private static String getUrl = "http://localhost:8202/productInfo/index/get";


    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS) //连接超时时间
                .writeTimeout(30,TimeUnit.SECONDS)  //请求超时时间
                .readTimeout(30,TimeUnit.SECONDS)  //响应超时时间
                .build();
        get(client);
//        asyncGet(client);
//        post(client);
//        asyncPost(client);
    }

    /**
     * 同步get
     * @param client
     * @return void
     */
    public static void get(OkHttpClient client){
//        创建get请求对象
        Request request = new Request.Builder()
                .url("http://localhost:8088/chat/test?c=redis使用")
                .get()
                .header("Content-Type", "application/json") // 设置Content-Type请求头
                .build();

        try {
//            返回响应对象
            Response response = client.newCall(request).execute();
//              验证响应是否成功
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//              解码对象
            ResponseBody body = response.body();
//            Object data = result.getData();
            System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步get
     * @param client
     * @return void
     */
    public static void asyncGet(OkHttpClient client){
        //        创建get请求对象
        Request request = new Request.Builder()
                .url(getUrl+"?type=1")
                .get()   //不指定请求方式默认是get
                .build();
//           异步发送请求，没有返回结果
        client.newCall(request).enqueue(new Callback() {

//               处理失败请求
            @Override
            public void onFailure(Call call, IOException e) {
                log.debug("Unexpected code " + e.getMessage());
                e.printStackTrace();
            }

//            处理成功请求
            @Override
            public void onResponse(Call call, Response response) throws IOException {
//              验证响应是否成功
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//              解码对象
                Result result = JSONUtil.toBean(response.body().string(), Result.class);
                Object data = result.getData();
                System.out.println(data);
            }
        });
    }


    /**
     * 同步post
     * @param client
     * @return void
     */
    public static void post(OkHttpClient client){
        User user = new User();
        user.setPost(1);

        String str = JSONUtil.toJsonStr(user);
        Request request = new Request.Builder()
                .url(postUrl)
                .header("Content-Type", "application/json") // 设置Content-Type请求头
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), str))
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            //              验证响应是否成功
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//              解码对象
            Result result = JSONUtil.toBean(response.body().string(), Result.class);
            Object data = result.getData();
            log.info("post请求成功，返回结果:{}",data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步post请求
     * @param client
     * @return void
     */
    public static void asyncPost(OkHttpClient client){
        User user = new User();
        user.setPost(1);
//          把对象转为json字符串
        String str = JSONUtil.toJsonStr(user);

        Request request = new Request.Builder()
                .url(postUrl)
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), str))
                .build();

        Call call = client.newCall(request);
//       异步请求没返回
        call.enqueue(new Callback() {
//            请求异常
            @Override
            public void onFailure(Call call, IOException e) {
                log.debug("Unexpected code " + e.getMessage());
                e.printStackTrace();
            }
//            请求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //              验证响应是否成功
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//              解码对象
                Result result = JSONUtil.toBean(response.body().string(), Result.class);
                Object data = result.getData();
                log.info("异步post请求成功，返回结果:{}",data);
            }
        });
    }
}
