package com.dzics.common.model.response.timeanalysis;

import lombok.Data;

import java.util.Date;

/**
 * 设备用时记录
 *
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
@Data
public class DeviceStateDetails {

    private String equipmentNo;
    /**
     * 归属安全门编号
     */
    private String doorCode;
    private String deviceType;
    private String equipmentName;
    private Date stopTime;
    private String resetTime;
    private String workState;
    private Long duration;
    private String groupId;
}
