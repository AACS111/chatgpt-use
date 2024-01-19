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
    private String apiKey = "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";

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

    public String getAnswer(WebClient webClient,String question){
        Mono<ChatGptResponseParameter> mono = webClient
                .post()
                .uri(apiUrl)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "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")
                .body(BodyInserters.fromValue(new ChatGptMessage("user", question)))
                .retrieve()
                .bodyToMono(ChatGptResponseParameter.class);

        ChatGptResponseParameter block = mono.block();
//        (parameter -> {
//            String ans = "";
//            parameter
//            for (Choices choice : parameter.getChoices()) {
//                ChatGptMessage message = choice.getMessage();
////                parameter.addMessages(new ChatGptMessage(message.getRole(), message.getContent()));
//                String s = message.getContent().replaceAll("\n+", "\n");
//                ans += s;
//            }
//        });
        List<Choices> choices = block.getChoices();
        Choices choices1 = choices.get(0);



        return null;
    }


    public  Flux<String> getAnswer(Session session,String str){
     WebClient webClient = WebClient.builder()
                .build();

        ChatGptMessage chatGptMessage = new ChatGptMessage("user", str);
        chatGptRequestParameter.addMessages(chatGptMessage);

        Flux<String> objectFlux = Flux.create(emitter -> {
            Flux<String> stringFlux = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .body(BodyInserters.fromValue(chatGptRequestParameter))
                    .body(BodyInserters.fromValue(jsonObject(str))) //参数
                    .retrieve()
                    .bodyToFlux(String.class)
                    .onErrorResume(WebClientResponseException.class, cg -> {

                        cg.getStatusCode();
                        String res = cg.getResponseBodyAsString();
                        System.out.println(res);
                        emitter.next(res); // 传递错误信息给订阅者
                        return Mono.empty(); // 返回一个空的 Mono，表示继续执行流
                    })
                    .doOnError(error -> System.err.println("Error: " + error))
                    .doOnComplete(() -> System.out.println("Done"));
            OpenAISubscriber openAISubscriber = new OpenAISubscriber(emitter);

            stringFlux.subscribe(openAISubscriber);
            emitter.onDispose(openAISubscriber);
        });
//
        objectFlux.subscribe(
                // 处理每个元素的回调
                item -> System.out.println("Received: " + item),
                // 处理错误的回调
                error -> System.err.println("Error: " + error),
                // 处理流完成的回调
                () -> System.out.println("Done")
        );
        return objectFlux;

    }

    public void getAnswer2(Session session, String str) {

        ChatGptMessage chatGptMessage = new ChatGptMessage("user", str);
        chatGptRequestParameter.addMessages(chatGptMessage);

        webClient().post()
                .accept(MediaType.TEXT_EVENT_STREAM) //接收text/event-stream流的数据
//                .body(BodyInserters.fromValue(chatGptRequestParameter)) //参数
                .body(BodyInserters.fromValue(jsonObject(str))) //参数
                .retrieve()
                .bodyToFlux(ChatGptResponseParameter.class) //输出格式
                .map(s -> {
                    if (!Objects.equals(s, "[DONE]")) {
                        log.info("Gpt输出：{}", s);
//                        ChatGptResponseParameter chatGptResponseParameter = JSONUtil.toBean(s, ChatGptResponseParameter.class);

                        ChatGptMessage message = s.getChoices().get(0).getDelta();
//                        JSONObject jo = JSON.parseObject(s).getJSONArray("choices").getJSONObject(0).getJSONObject("delta");
//                        String content = jo.getString("content");
//                        if (message != null) {

//                            try {
//                                session.getBasicRemote().sendText(content);
                                log.info("Gpt输出：{}", s);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            return content;
//                        }
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
