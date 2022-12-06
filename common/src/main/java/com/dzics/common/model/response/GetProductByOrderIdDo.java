package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetProductByOrderIdDo {
    @ApiModelProperty("唯一属性")
    private Long productId;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("产品序号，对接底层定义值使用,产品id")
    private String productNo;
}
