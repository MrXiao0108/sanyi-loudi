package com.dzics.business.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeviceParms {
    /**
     * 订单序号
     */
    @ApiModelProperty(value = "订单序号",required = true)
    @NotBlank(message = "订单必选择")
    private String orderNo;
    /**
     * 产线序号
     */
    @ApiModelProperty(value = "产线序号",required = true)
    @NotBlank(message = "产线必选")
    private String lineNo;
}
