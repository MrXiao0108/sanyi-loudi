package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetToolInfoDataDo {
    @ApiModelProperty("设备id")
    private Long equipmentId;
    @ApiModelProperty("设备名称")
    private String equipmentName;
    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("刀具信息")
    List<ToolDataDo> toolDataList;


}
