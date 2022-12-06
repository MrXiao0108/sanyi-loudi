package com.dzics.business.model.vo.udp;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/5/18.
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
