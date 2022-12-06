package com.dzics.common.model.response.charts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivationDetailsDo {

    @ApiModelProperty("时间日期")
    private String dateData;
    @ApiModelProperty("稼动率")
    private BigDecimal activationData;
}
