package com.dzics.common.model.response.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/12/7.
 * @since
 */
@Data
public class GetOrderCfig {

    @ApiModelProperty("是否完成")
    private Boolean orderStatus;

    @ApiModelProperty(value = "主键",required = true)
    @NotNull(message = "订单ID必填")
    private String configOrderId;

    @ApiModelProperty(value = "RFID信息",required = true)
    @NotNull(message = "RFID信息必填")
    private String rfid;

    @ApiModelProperty(value = "订单号",required = true)
    @NotNull(message = "订单号必填")
    private String orderNum;

    @ApiModelProperty(value = "物料号",required = true)
    @NotNull(message = "物料号必填")
    private String materialCode;

    @ApiModelProperty("创建时间")
    private String createTime;
}
