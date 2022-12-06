package com.dzics.common.model.mom.response;

import lombok.Data;

@Data
public class RequestHeaderVo<T> {
    //版本  协议 必填
    private Integer version;
    //消息ID  随机生成32位UUID，单次下发指令唯一标识   必填
    private String taskId;
    //接口类型 固定为1     必填
    private String taskType;
    //消息内容   json串    必填

    private T reported;

}
