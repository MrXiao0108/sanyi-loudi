package com.dzics.udp.netty.util;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/4/22.
 * @since
 */
@Data
public class RespMessage {
    private int code;
    private String message;
}
