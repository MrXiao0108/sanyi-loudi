package com.dzics.common.model.response.commons;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共订单
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class Orders {
    @ApiModelProperty("订单ID")
    private String orderId;
    @ApiModelProperty("站点ID")
    private String departId;
    @ApiModelProperty("订单序号ID")
    private String orderNo;
    @ApiModelProperty("订单名称")
    private String orderName;
    @ApiModelProperty("站点名称")
    private String departName;

    @ApiModelProperty("产线名称")
    private String lineName;
}
