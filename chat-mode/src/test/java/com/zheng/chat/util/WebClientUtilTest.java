package com.zheng.chat.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/7 16:44:58
 */
@SpringBootTest
class WebClientUtilTest {

    @Resource
    private WebClientUtil webClientUtil;
    @Test
    void test(){
        WebClient webClient = WebClient.create();
//        webClientUtil.get(webClient);

//        webClientUtil.asyncGet();

//        webClientUtil.post();
        webClientUtil.asyncPost();
    }

}