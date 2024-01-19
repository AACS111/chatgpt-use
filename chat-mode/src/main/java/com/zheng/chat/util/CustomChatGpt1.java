package com.zheng.chat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zheng.chat.model.ChatGptMessage;
import com.zheng.chat.model.ChatGptRequestParameter;
import com.zheng.chat.model.ChatGptResponseParameter;
import com.zheng.chat.model.Choices;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----连接---
 * @date 2024/1/3 19:51:42
 */
@Component
public class CustomChatGpt1 {

//    @Value("${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}")
    private String apiKey = "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";
//    @Value("${https://api.openai.com/v1/chat/completions}")
//    private String model = "gpt-3.5-turbo-0301";
    private String model = "gpt-3.5-turbo";
//    @Value("${gpt-3.5-turbo-0301}")
    private String apiUrl = "https://api.openai.com/v1/chat/completions";

    private int responseTimeout = 10000;

    private Charset charset = StandardCharsets.UTF_8;


    private ChatGptRequestParameter chatGptRequestParameter = new ChatGptRequestParameter();




    public String getAnswer(CloseableHttpClient client,String question){
        // 创建一个HttpPost
        HttpPost httpPost = new HttpPost(apiUrl);
        // 创建一个ObjectMapper，用于解析和创建json
        ObjectMapper objectMapper = new ObjectMapper();


        // 设置请求参数
        chatGptRequestParameter.addMessages(new ChatGptMessage("user", question));
        HttpEntity httpEntity = null;
        try {
            // 对象转换为json字符串
            httpEntity = new StringEntity(objectMapper.writeValueAsString(chatGptRequestParameter), charset);
        } catch (JsonProcessingException e) {
            System.out.println(question + "->json转换异常");
            return null;
        }
        httpPost.setEntity(httpEntity);
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // 设置请求头
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        // 设置登录凭证
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        // 用于设置超时时间
        RequestConfig config = RequestConfig
                .custom()
                .setResponseTimeout(responseTimeout, TimeUnit.MILLISECONDS)
                .build();
        httpPost.setConfig(config);
        try {
            // 提交请求
            return client.execute(httpPost, response -> {
                // 得到返回的内容
                String resStr = EntityUtils.toString(response.getEntity(), charset);
                // 转换为对象
                ChatGptResponseParameter responseParameter = objectMapper.readValue(resStr, ChatGptResponseParameter.class);
                String ans = "";
                // 遍历所有的Choices（一般都只有一个）
                for (Choices choice : responseParameter.getChoices()) {
                    ChatGptMessage message = choice.getDelta();
                    chatGptRequestParameter.addMessages(new ChatGptMessage(message.getRole(), message.getContent()));
                    String s = message.getContent().replaceAll("\n+", "\n");
                    ans += s;
                }
                // 返回信息
                return ans;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发生异常，移除刚刚添加的ChatGptMessage
        chatGptRequestParameter.getMessages().remove(chatGptRequestParameter.getMessages().size()-1);
        return "您当前的网络无法访问";



    }


    public String printAnswer(ChatGptRequestParameter chatRequestParameter,String question){
        // 创建一个HttpPost
        HttpPost httpPost = new HttpPost(apiUrl);

//        httpPost.sta
        return null;
    }




}



