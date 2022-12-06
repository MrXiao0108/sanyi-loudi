package com.dzics.sanymom.model.request.sany;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * MOM 下发的订单参数信息
 * @author NeverEnd
 */
@Data
public class IssueOrderInformation {

    /**
     *接口类型
     */
    private String taskType;
    /**
     * 消息内容
     */
    @NotNull(message = "内容不能为空")
    private Task task;
    /**
     * 协议版本
     */
    private int version;
    /**
     * 消息ID
     */
    private String taskId;


}
