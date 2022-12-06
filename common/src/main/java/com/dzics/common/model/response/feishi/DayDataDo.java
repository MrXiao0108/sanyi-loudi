package com.dzics.common.model.response.feishi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DayDataDo {
    @ApiModelProperty("合格")
    private List<BigDecimal> qualifiedNum;
    @ApiModelProperty("不DayDataDo良")
    private List<BigDecimal> badnessNum;
}
