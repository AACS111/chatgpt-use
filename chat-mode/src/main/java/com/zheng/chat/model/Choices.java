package com.zheng.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/3 19:49:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Choices {
    ChatGptMessage delta;
    String finish_reason;
    Integer index;
    String logprobs;

}
