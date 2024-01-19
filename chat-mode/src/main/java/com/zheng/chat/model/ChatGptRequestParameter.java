package com.zheng.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----请求参数---
 * @date 2024/1/3 19:47:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptRequestParameter {
    private String model = "gpt-3.5-turbo-1106";
//     是否支持流式输出
    private boolean stream = true;
    private Integer maxTokens = 2048;

    List<ChatGptMessage> messages=new ArrayList();



    public void addMessages(ChatGptMessage message) {
        this.messages.add(message);
    }


}
