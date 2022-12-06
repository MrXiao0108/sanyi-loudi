package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthData {
    @ApiModelProperty("月份")
    private String month;
    @ApiModelProperty("合格")
    private Long qualified;
    @ApiModelProperty("不合格")
    private Long rejects;
}
