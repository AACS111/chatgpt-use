package com.zheng.chat.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----页面---
 * @date 2024/1/16 20:02:43
 */
@Controller
public class ChatWeb {


    @RequestMapping("/webSocket")
    public String webSocket(){
        return "webSocket";
    }

    @RequestMapping("/webSocket2")
    public String webSocket2(){
        return "webSocket2";
    }

}
