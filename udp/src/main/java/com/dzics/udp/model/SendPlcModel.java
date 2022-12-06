package com.dzics.udp.model;

import lombok.Data;

/**
 * 发送给PlC消息分封装模板
 *
 * @author ZhangChengJun
 * Date 2021/4/6.
 * @since
 */
@Data
public class SendPlcModel {
    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;
    /**
     * 发送的消息
     */
    private String message;
}
