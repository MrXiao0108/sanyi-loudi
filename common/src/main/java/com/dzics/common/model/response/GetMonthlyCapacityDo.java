package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetMonthlyCapacityDo {
    @ApiModelProperty("合格")
    private Long qualified;
    @ApiModelProperty("不合格")
    private Long badness;
    private String datelist;
}
