package com.dzics.common.model.response.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LineProductionDataDo {
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("产线id")
    private Long lineId;
    @ApiModelProperty("生产数量")
    private String DataSum;
    private Long planId;
}
