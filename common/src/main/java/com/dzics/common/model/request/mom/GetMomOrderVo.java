package com.dzics.common.model.request.mom;


import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetMomOrderVo extends PageLimit {

    @ApiModelProperty("生产任务订单类型(1：正常订单；2：返工返修订单)")
    private String wipOrderType;

    @ApiModelProperty("订单状态(110已下达 120进行中 130已完工 140已删除 150强制关闭)")
    private String progressStatus;

    @ApiModelProperty("生产任务订单号")
    private String wipOrderNo;

    @ApiModelProperty("产线id")
    private String lineId;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("起始时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty("结束时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;

}
