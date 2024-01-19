package com.zheng.chat.config;

import lombok.Getter;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----返回结果---
 * @date 2023/12/23 16:02:49
 */
@Getter
public enum  ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    ;

    private Integer code;
    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

