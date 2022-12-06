package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PutEquipmentDataStateVo {
    @ApiModelProperty("设备id")
    @NotNull(message = "设备id不能为空")
    private Long id;
    @NotBlank(message = "name不能为空")
    private String name;
    @Min(0)
    @Max(1)
    @NotNull(message = "value不能为空")
    private Long value;
//    @ApiModelProperty("当日设备产量分析 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbyOne;
//    @ApiModelProperty("预留字段2 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbyTwo;
//    @ApiModelProperty("预留字段3 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbyThree;
//    @ApiModelProperty("预留字段4 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbyFour;
//    @ApiModelProperty("预留字段5 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbyFive;
//    @ApiModelProperty("预留字段6 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbySix;
//    @ApiModelProperty("预留字段7 0隐藏 1显示")
//    @Min(0)
//    @Max(1)
//    private Integer standbySeven;
}
