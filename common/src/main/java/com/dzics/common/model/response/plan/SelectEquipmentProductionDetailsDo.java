package com.dzics.common.model.response.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectEquipmentProductionDetailsDo {
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("生产数量")
    private Long productionQuantity;
    @ApiModelProperty("产品类型")
    private String productType;
}
