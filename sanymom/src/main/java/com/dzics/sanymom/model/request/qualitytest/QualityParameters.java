package com.dzics.sanymom.model.request.qualitytest;

import lombok.Data;

@Data
public class QualityParameters {


    //接口类型
    private String taskType;
    //消息内容
    private QualityReported reported;
    //协议版本
    private Integer version;
    //消息ID
    private String taskId;
}
