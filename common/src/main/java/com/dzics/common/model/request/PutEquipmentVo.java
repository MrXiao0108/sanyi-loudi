package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PutEquipmentVo {


    @ApiModelProperty("安全门编号")
    private String doorCode;


    @NotNull(message = "请选择设备")
    private Long id;

    @ApiModelProperty("设备名称")
    @NotEmpty(message = "设备名称必填")
    private String equipmentName;

    @ApiModelProperty("备注")
    private String postscript;

    @ApiModelProperty("产线id")
    @NotNull(message = "请选择产线")
    private Long lineId;

    @ApiModelProperty("设备类型(1检测设备,2机床,3机器人,10 门,添加修改设备不用管)")
    @NotNull(message = "设备类型必填")
    private Integer equipmentType;

    @ApiModelProperty("设备序号")
    @NotEmpty(message = "设备序号必填")
    private String equipmentNo;

    @ApiModelProperty("设备编号")
    @NotEmpty(message = "设备编号必填")
    private String equipmentCode;

//    @ApiModelProperty("设备名称")
//    @NotEmpty(message = "设备名称不能为空")
//    private String equipmentName;
//
    @ApiModelProperty("订单编号,不用管此参数")
    private String orderNo;

    @ApiModelProperty("设备别称")
    @NotEmpty(message = "设备别称不能为空")
    private String nickName;

    @ApiModelProperty("是否看板显示该设备(0不显示，1显示)")
    @NotNull(message = "isShow不能为空")
    @Min(0)
    @Max(1)
    private Integer isShow;

    @ApiModelProperty("设备作业率")
    @Min(0)
    @Max(100)
    private Float standardOperationRate;

}
