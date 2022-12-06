package com.dzics.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


/**
 * @param
 * @author zhangchengjun
 */
@Data
public class PageLimitAgv {
    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;

    @ApiModelProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号必传")
    private String orderNo;
    @ApiModelProperty(value = "产线号", required = true)
    @NotBlank(message = "产线号必传")
    private String lineNo;
}
