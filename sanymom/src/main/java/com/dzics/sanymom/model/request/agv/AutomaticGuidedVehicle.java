package com.dzics.sanymom.model.request.agv;

import lombok.Data;

/**
 * agv搬运信息反馈
 */
@Data
public class AutomaticGuidedVehicle {
    /**
     * 协议版本
     */
    private int version;
    /**
     * 消息ID
     */
    private String taskId;
    /**
     * 接口类型
     */
    private String taskType;
    /**
     * 消息内容
     */
    private AgvTask task;

}
