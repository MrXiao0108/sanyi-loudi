package com.dzics.common.model.request.mom;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class LineProductionDataVO {
    @ApiModelProperty("产线id")
    private List<String> ids;
    @ApiModelProperty("起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
