package com.dzics.common.model.response.charts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OperationDo {
    private Long equipmentId;
    private String equipmentName;
//    private Integer equipmentType;
    @ApiModelProperty("true展示，false不展示")
    private boolean isShow;
    @ApiModelProperty("设备运行率")
    private List<BigDecimal> equipmentData;
}
