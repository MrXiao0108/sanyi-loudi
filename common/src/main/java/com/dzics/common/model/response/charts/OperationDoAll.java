package com.dzics.common.model.response.charts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OperationDoAll {
    List<String> date;
    @ApiModelProperty("设备运行率")
    private List<OperationDo> operationDos;
}
