package com.zheng.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----启动---
 * @date 2024/1/3 19:27:20
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * 服务器端点导出
     * @author zhengfuping
     * @date 2023/8/22
     * @return ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter getServerEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
