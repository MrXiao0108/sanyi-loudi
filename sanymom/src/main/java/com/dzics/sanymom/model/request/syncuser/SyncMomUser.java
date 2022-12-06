package com.dzics.sanymom.model.request.syncuser;

import com.dzics.sanymom.model.request.agv.AgvTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class SyncMomUser {
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

    @JsonProperty(value = "SysCode")
    private String SysCode;
    /**
     * 消息内容
     */
    private SyncMomUserTask task;
}
