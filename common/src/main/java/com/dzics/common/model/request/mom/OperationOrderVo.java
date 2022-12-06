package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OperationOrderVo {

    @ApiModelProperty(value = "订单ID")
    @NotBlank(message = "订单ID不能为空")
    private String ProTaskOrderId;

    @ApiModelProperty(value = "订单状态(开始订单120、暂停订单160)")
    @NotBlank(message = "订单状态不能为空")
    private String ProgressStatus;
}
