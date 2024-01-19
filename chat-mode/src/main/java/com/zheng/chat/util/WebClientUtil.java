package com.zheng.chat.util;

import cn.hutool.json.JSONUtil;
import com.zheng.chat.config.Result;
import com.zheng.chat.model.User;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/7 16:43:50
 */
@Component
@Slf4j
public class WebClientUtil {
    private static String postUrl = "http://localhost:8202/productInfo/index/post";
    private static String getUrl = "http://localhost:8202/productInfo/index/get";



    /**
     * 同步执行get请求
     * @param webClient
     * @return void
     */
    public void get(WebClient webClient){
        Mono<Result> mono = webClient.get()
                .uri(getUrl + "?type=2")
                .retrieve()
                .bodyToMono(Result.class);

//        获取返回结果 block()会进行堵塞，等待返回结果
        Result result = mono.block();
        if (result.getCode()==200){
            log.info("get请求返回结果{}",result.getData());
        }
    }

    /**
     * 异步get
     * @return void
     */
    public void asyncGet(){
        HttpClient client = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  //设置连接超时
                .doOnConnected(conn -> conn
                        .addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS)) //写入超时
                        .addHandler(new WriteTimeoutHandler(10))); //读取超时


        //        也可以以这种方式创建WebClient可以添加请求头和url以及一些参数；
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(getUrl)

                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)")
//                .defaultCookie()  //添加请求Cookie
                .build();

//        获取返回结果
        Mono<Result> mono = webClient.get()
                .uri( "?type=2")// 请求路径，会拼接上面的
                .accept(MediaType.APPLICATION_JSON)

//                .retrieve() //获取响应体
//                .bodyToMono(Result.class);  //指定获取的类型,直接获取结果。

//                或者通过该方式手动处理结果
                .exchangeToMono(response->{
                    if (response.statusCode().equals(HttpStatus.OK)) {
//                        成功返回
                        return response.bodyToMono(Result.class);
                    }
                    else {
//                        失败返回
                        return response.createException().flatMap(Mono::error);
                    }
                });


//        异步获取返回结果
        mono
                .timeout(Duration.ofSeconds(60))
                .subscribe(result -> {

                    if (result.getCode() == 200) {
                        log.info("get请求返回结果{}", result.getData());
                    }
                });

        System.out.println("执行完成");

    }

    /**
     * 同步post
     * @return void
     */
    public void post(){
        // 创建 WebClient 对象
        WebClient webClient = WebClient.builder()
                .baseUrl(getUrl)
                .build();
        User user = new User();
        user.setPost(2);

        Mono<Result> mono = webClient
                .post()
                .uri(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","12321412")
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(Result.class);
        Result result = mono.block();
        if (result.getCode()==200){
            log.info("post请求返回结果{}",result.getData());
        }
    }


    /**
     * WebClient异步请求
     * @return void
     */
    public void asyncPost(){
        // 创建 WebClient 对象
        WebClient webClient = WebClient.builder()
                .baseUrl(getUrl)
                .build();
        User user = new User();
        user.setPost(2);

        Mono<Result> mono = webClient
                .post()
                .uri(postUrl)
                .contentType(MediaType.APPLICATION_JSON)  //指定类型
                .header("token","12321412")  //添加请求头
                .body(BodyInserters.fromValue(user)) //添加请求对象
                .retrieve()
                .bodyToMono(Result.class);
//          异步请求
        mono.subscribe(result -> {
            if (result.getCode()==200){
                log.info("post异步请求返回结果{}",result.getData());
            }
        });
    }
}
