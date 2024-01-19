package com.zheng.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/3 19:50:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usage {

    String prompt_tokens;
    String completion_tokens;
    String total_tokens;
}
