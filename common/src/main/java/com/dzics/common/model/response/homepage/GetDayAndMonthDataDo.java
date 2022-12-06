package com.dzics.common.model.response.homepage;

import com.dzics.common.model.response.feishi.DayDataDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetDayAndMonthDataDo {

    @ApiModelProperty("日产")
    private DayDataDo dayDataDo;
    @ApiModelProperty("月产")
    private DayDataDo monthDataDo;
}
