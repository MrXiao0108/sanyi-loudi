package com.dzics.common.model.request.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderLinePrmsWork {
    /**
     *
     */
    @ApiModelProperty("订单ID")
    private String orderId;
    @ApiModelProperty("产线Id")
    private String lineId;
    @ApiModelProperty("工位-工件关联关系ID")
    private String workStationProductId;
}
