package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EquipmentAvailableDo {

    @ApiModelProperty("设备名称")
    private List<String> eqName;
    @ApiModelProperty("运行时间")
    private List<String> timeRun;
    @ApiModelProperty("停机时间")
    private List<String> stopTime;
}
