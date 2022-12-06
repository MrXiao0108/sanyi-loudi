package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentTimeAnalysisDo {

    @ApiModelProperty("设备名称")
    private String eqName;
    @ApiModelProperty("运行时间")
    private String timeRun;
    @ApiModelProperty("停机时间")
    private String stopTime;
    @ApiModelProperty("设备序号")
    private String eqNo;
    @ApiModelProperty("设备id")
    private String equimentId;
}
