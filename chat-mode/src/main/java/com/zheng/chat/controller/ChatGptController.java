package com.zheng.chat.controller;

import com.zheng.chat.test.WebChatGPT;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/9 19:26:16
 */

@RestController
@RequestMapping("/chat")
public class ChatGptController {
    @Resource
    private WebChatGPT webChatGPT;




//    @GetMapping("/write")
//    public String write(String str){
//        return webChatGPT.getAnswer(str);
//    }


    @GetMapping(value ="/test")
    public Flux<String> stringFlux(String c) {
        Flux<String> flux = webChatGPT.webChat(c);
        return flux;
    }



}
