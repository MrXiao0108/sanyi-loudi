package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddOrderVo {
    @ApiModelProperty("id(编辑必填)")
    public Long id;


    @ApiModelProperty("订单编号")
    @NotEmpty(message = "订单编号不能为空")
    public String orderNo;

    @ApiModelProperty("站点id")
    @NotNull(message = "请选择站点")
    public Long departId;

    @ApiModelProperty("备注")
    public String remarks;


}
