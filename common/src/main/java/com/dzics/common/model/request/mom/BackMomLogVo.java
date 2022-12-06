package com.dzics.common.model.request.mom;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BackMomLogVo extends PageLimit {
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产线编号")
    private String lineNo;

    @ApiModelProperty(value = "日志类型 (序列号查询订单号、更新料点状态、查询料框接口、移出满料框、发送检测数据、请求MOM报工、请求叫料、请求空料框、空料框移出)")
    private String logType;

//    @ApiModelProperty(value = "Mom订单号")
//    private String WipOrderNo;

//    @ApiModelProperty(value = "发送状态  (成功、失败)")
//    private String status;

    @ApiModelProperty(value = "开始搜索时间")
    private String beginTime;

    @ApiModelProperty(value = "结束搜索时间")
    private String endTime;

    @ApiModelProperty(value = "关键字")
    private String crux;
}
