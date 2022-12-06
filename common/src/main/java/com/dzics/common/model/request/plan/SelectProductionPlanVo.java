package com.dzics.common.model.request.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectProductionPlanVo {

    @ApiModelProperty("站点名称")
    private String departName;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("不用填")
    private String orgCode;
    private Integer planType;
    @ApiModelProperty("产线id")
    private String lineId;
    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
