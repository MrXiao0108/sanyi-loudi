package com.dzics.common.model.request.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderLinePrms {
    /**
     *
     */
    @ApiModelProperty("订单ID")
    private String orderId;
    @ApiModelProperty("产线Id")
    private String lineId;
    @ApiModelProperty("产品编号")
    private String productNo;
}
