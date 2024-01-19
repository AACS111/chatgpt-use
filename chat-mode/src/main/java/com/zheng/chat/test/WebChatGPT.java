package com.zheng.chat.test;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zheng.chat.listener.OpenAISubscriber;
import com.zheng.chat.model.ChatGptMessage;
import com.zheng.chat.model.ChatGptRequestParameter;
import com.zheng.chat.model.ChatGptResponseParameter;
import com.zheng.chat.model.Choices;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----测试调用---
 * @date 2024/1/9 18:31:29
 */


@Component
@Slf4j
public class WebChatGPT {

    /**
     * 自己chatGpt的ApiKey
     */
    private String apiKey = "sk-***";

    private ChatGptRequestParameter chatGptRequestParameter = new ChatGptRequestParameter();


    private Session session;

    /**
     * 使用的模型
     */
    private String model = "gpt-3.5-turbo-0301";
    /**
     * 对应的请求接口
     */
    private String apiUrl = "https://api.openai.com/v1/chat/completions";


    /**
     * 推送
     * @param session
     * @param str
     * @return void
     * @author 郑福平2403
     * @date 2024/1/19 10:30:16
     */
    public void getAnswer2(Session session, String str) {

        ChatGptMessage chatGptMessage = new ChatGptMessage("user", str);
        chatGptRequestParameter.addMessages(chatGptMessage);

        webClient().post()
                .accept(MediaType.TEXT_EVENT_STREAM) //接收text/event-stream流的数据
//                .body(BodyInserters.fromValue(chatGptRequestParameter)) //参数
                .body(BodyInserters.fromValue(jsonObject(str))) //参数
                .retrieve()
                .bodyToFlux(String.class) //输出格式
                .map(s -> {
                    if (!Objects.equals(s, "[DONE]")) {
                        log.info("Gpt输出：{}", s);
//                        ChatGptResponseParameter chatGptResponseParameter = JSONUtil.toBean(s, ChatGptResponseParameter.class);

//                        ChatGptMessage message = chatGptResponseParameter.getChoices().get(0).getDelta();
                        JSONObject jo = JSON.parseObject(s).getJSONArray("choices").getJSONObject(0).getJSONObject("delta");
                        String content = jo.getString("content");
                        if (content != null) {

                            try {
                                session.getBasicRemote().sendText(content);
                                    log.info("Gpt输出：{}", s);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return content;
                        }
                    }
                    return "";
                })
                .onErrorResume(WebClientResponseException.class, ex -> Flux.just(ex.getResponseBodyAsString())) //请求失败
                .doFinally(signalType -> log.info("完成")).subscribe(); //请求完成后
    }

    private String getString(ChatGptResponseParameter response) {
        String str = "";
        for (Choices choice : response.getChoices()) {
            ChatGptMessage message = choice.getDelta();
            chatGptRequestParameter.addMessages(new ChatGptMessage(message.getRole(), message.getContent()));
            String s = message.getContent().replaceAll("\n+", "\n");
            str += s;
        }
        return str;
    }


    public Flux<String> webChat(String c){
        return webClient().post()
                .accept(MediaType.TEXT_EVENT_STREAM) //接收text/event-stream流的数据
                .body(BodyInserters.fromValue(jsonObject(c))) //参数
                .retrieve()
                .bodyToFlux(String.class) //输出格式
                .map(s -> {
                    if (!Objects.equals(s, "[DONE]")) {
                        JSONObject jo = JSON.parseObject(s).getJSONArray("choices").getJSONObject(0).getJSONObject("delta");
                        String content = jo.getString("content");
                        if(content != null){
                            log.info("Gpt输出：{}", JSON.parseObject(s).getJSONArray("choices"));
                            return content;
                        }
                    }
                    return "";
                })
                .onErrorResume(WebClientResponseException.class, ex -> Flux.just(ex.getResponseBodyAsString())) //请求失败
                .doFinally(signalType -> log.info("完成")); //请求完成后

    }


    //参数
    private JSONObject jsonObject(String content){
        JSONObject jsonObject = new JSONObject();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role","user");
        userMessage.put("content",content);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(userMessage);
        jsonObject.put("model", "gpt-3.5-turbo-16k-0613");  //速度快，价格高
        jsonObject.put("messages", jsonArray);
        jsonObject.put("stream", true);
        return jsonObject;
    }


    private WebClient webClient(){
        return  WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create().proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host("127.0.0.1").port(1080)) //代理
//                ))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .baseUrl("https://api.openai.com/v1/chat/completions") //请求地址
                .build();
    }

}
