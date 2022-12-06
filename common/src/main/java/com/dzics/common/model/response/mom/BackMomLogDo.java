package com.dzics.common.model.response.mom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BackMomLogDo {
    @ApiModelProperty(value = "日志ID")
    private String logId;

    @ApiModelProperty(value = "产线名称")
    private String lineName;

    @ApiModelProperty(value = "接口类型")
    private String details;

    @ApiModelProperty(value = "请求参数")
    private String InvokParm;

    @ApiModelProperty(value = "返回信息")
    private String InvokReturn;

    @ApiModelProperty(value = "发送时间")
    private String StartTime;

    @ApiModelProperty(value = "耗时")
    private String InvokCost;
//
//    @ApiModelProperty(value = "调用状态")
//    private String InvokStatus;

    @JsonIgnore
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
}
