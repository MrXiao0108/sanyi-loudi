package com.dzics.common.model.response.equipmentstate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.entity.DzDataCollection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DzDataCollectionDo extends DzDataCollection {

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    private Integer equipmentType;
    @ApiModelProperty(value = "设备序号")
    private String  equipmentNo;
    @ApiModelProperty(value = "设备名称")
    private String equipmentName;
    @ApiModelProperty("压头上下位置")
    private String headPositionUd;
    @ApiModelProperty("压头左右位置")
    private String headPostionLr;
    @ApiModelProperty("淬火机 移动速度  mm/s")
    private String movementSpeed;
    @ApiModelProperty("工件转速 Rad/min")
    private String workpieceSpeed;

    @ApiModelProperty("冷却液温度 ℃")
    private String coolantTemperature;
    @ApiModelProperty("冷却液压力 MPa")
    private String coolantPressure;
    @ApiModelProperty("冷却液流量 L/s")
    private String coolantFlow;
    @ApiModelProperty("坐标")
    private String currentLocation;
    @ApiModelProperty("停机次数")
    private Long downSum = 0L;
    @ApiModelProperty("设备id")
    private String equipmentId;


}
