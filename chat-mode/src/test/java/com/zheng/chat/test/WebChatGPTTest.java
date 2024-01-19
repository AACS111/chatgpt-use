package com.zheng.chat.test;

import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/9 18:59:29
 */
@SpringBootTest
class WebChatGPTTest {

    @Resource
    private WebChatGPT webChatGPT;
    private String apiUrl = "https://api.openai.com/v1/chat/completions";

    private String apiKey = "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";
    @Test
    public void test(){
        WebClient webClient = WebClient.builder()
                .build();



//        webChatGPT.getAnswer(webClient,"你是谁");

//        webChatGPT.getAnswer(webClient);
    }

}