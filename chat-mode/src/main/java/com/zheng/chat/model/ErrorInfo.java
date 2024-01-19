package com.zheng.chat.model;

import lombok.Data;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----异常---
 * @date 2024/1/3 20:51:05
 */
@Data
public class ErrorInfo {
    private String message;
    private String type;
    private String param;
    private String code;
}
