package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PlanAnalysisDo {

    @ApiModelProperty("完成率")
    private List<BigDecimal> percentageComplete;
    @ApiModelProperty("产出率")
    private List<BigDecimal> outputRate;
    @ApiModelProperty("合格率")
    private List<BigDecimal> passRate;
    @ApiModelProperty("日期")
    private List<String> dateList;
}
