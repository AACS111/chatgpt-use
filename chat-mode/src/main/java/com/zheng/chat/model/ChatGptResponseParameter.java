package com.zheng.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----响应对象---
 * @date 2024/1/3 19:49:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptResponseParameter {
    String id;
    String object;
    String created;
    String model;
    Usage usage;
    List<Choices> choices;
    String system_fingerprint;
}
