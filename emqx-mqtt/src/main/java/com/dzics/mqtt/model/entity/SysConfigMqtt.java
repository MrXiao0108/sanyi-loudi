package com.dzics.mqtt.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * mqtt 连接信息
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-02
 */
@Data
public class SysConfigMqtt implements Serializable {

    private static final long serialVersionUID = 1L;


    private String uuid;

    private String type;

    private String version;

    private String author;

    private String issue;

    private String due;

    private String platform_id;

    private String namespace;

    private String productKey;

    private String productType;

    private String server;

    private String accesskey;

    private String secretkey;

    private String pubkey;

    private String description;

    private String signature;

    /**
     * dev 测试环境 pro 正式环境
     */
    private String springProfilesActive;

    /**
     * 连接客户端id
     */
    private String clientId;

    /**
     * 数采软件上报状态
     */
    private String topicSoft;

    /**
     * 接收数据
     * 缓存数据传输
     */
    private String topicGateway;
}
