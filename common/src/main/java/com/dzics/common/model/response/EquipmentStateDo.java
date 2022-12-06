package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentStateDo {
    @ApiModelProperty("设备id")
    private Long id;
    @ApiModelProperty("产线名")
    private String lineName;
    @ApiModelProperty("设备名")
    private String equipmentName;
    @ApiModelProperty("当日设备产量分析 0隐藏 1显示")
    private Integer standbyOne;
    @ApiModelProperty("预留字段2 0隐藏 1显示")
    private Integer standbyTwo;
    @ApiModelProperty("预留字段3 0隐藏 1显示")
    private Integer standbyThree;
    @ApiModelProperty("预留字段4 0隐藏 1显示")
    private Integer standbyFour;
    @ApiModelProperty("预留字段5 0隐藏 1显示")
    private Integer standbyFive;
    @ApiModelProperty("预留字段6 0隐藏 1显示")
    private Integer standbySix;
    @ApiModelProperty("预留字段7 0隐藏 1显示")
    private Integer standbySeven;
}
