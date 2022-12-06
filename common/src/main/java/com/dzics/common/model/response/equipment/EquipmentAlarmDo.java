package com.dzics.common.model.response.equipment;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentAlarmDo {
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;
    @ExcelProperty("设备名称")
    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ExcelProperty("设备号")
    @ApiModelProperty("设备号")
    private String equipmentNo;
    @ExcelProperty("告警内容")
    @ApiModelProperty("告警内容")
    @ColumnWidth(30)
    private String itemText;

    @ExcelProperty("告警开始时间")
    @ApiModelProperty("告警开始时间")
    @ColumnWidth(25)
    private String stopTime;
    @ExcelProperty("告警结束时间")
    @ApiModelProperty("告警结束时间")
    @ColumnWidth(25)
    private String resetTime;
    @ExcelProperty("告警时长")
    @ApiModelProperty("告警时长")
    @ColumnWidth(25)
    private String durationStr;
    @ExcelIgnore
    @ApiModelProperty("告警时长")
    private Long duration;
    @ExcelIgnore
    @ApiModelProperty("告警类型编码")
    private String itemValue;
}
