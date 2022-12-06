package com.dzics.common.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GetByEquipmentNoDo {


    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("产线名称")
    private String lineName;

    @ApiModelProperty(value = "设备序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    private Integer equipmentType;

    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    @ApiModelProperty(value = "设备名称")
    private String equipmentName;

    @ApiModelProperty(value = "开始停机时间")
    private Date stopTime;
    @ApiModelProperty(value = "恢复运行时间")
    private Date resetTime;
    @ApiModelProperty(value = "停机日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date stopData;

    @ApiModelProperty(value = "停机日期时长")
    private Long duration;

    @ApiModelProperty(value = "分组id")
    private Long groupId;

}
