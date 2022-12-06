package com.dzics.mqtt.model.mdc;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 软件上线上报
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Data
public class MdcModel {

    /**
     * 指 令 标 识
     */
    private String cmdId;
    /**
     * 必填，值为1，，与证书中的version值相同
     */
    private int version;
    /**
     * 必填，值为1
     */
    private int sequenceId = 1;
    /**
     * 必填，数据上报时MDC软件的时刻，精确到秒
     */
    private Long edgeTime;
    /**
     * 表示设备类型，设备类型在IoT平台上维护
     */
    private String productKey;
    /**
     * 设备编号
     */
    private String clientUuid;

    private List<Map<String, Object>> reported;
}
