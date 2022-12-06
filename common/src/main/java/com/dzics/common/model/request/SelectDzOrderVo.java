package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectDzOrderVo {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("站点名")
    private String departName;


    @ApiModelProperty("站点id")
    private Integer departId;
}
