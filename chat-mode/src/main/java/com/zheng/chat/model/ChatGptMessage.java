package com.zheng.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----请求对象---
 * @date 2024/1/3 19:46:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptMessage {

    String role;
    String content;
}
