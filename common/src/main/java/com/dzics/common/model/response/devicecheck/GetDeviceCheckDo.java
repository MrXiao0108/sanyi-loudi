package com.dzics.common.model.response.devicecheck;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GetDeviceCheckDo {
    @ApiModelProperty("主键")
    @ExcelIgnore
    private String checkHistoryId;
    @ApiModelProperty("产线名称")
    @ExcelProperty(value = "产线名称")
    private String lineName;
    @ApiModelProperty("产线id")
    @ExcelIgnore
    private Long lineId;
    @ApiModelProperty("设备名称")
    @ExcelProperty(value = "设备名称")
    private String equipmentName;
    @ApiModelProperty("设备编号")
    @ExcelProperty(value = "设备编号")
    private String equipmentNo;
    @ApiModelProperty("巡检单号")
    @ExcelProperty(value = "巡检单号")
    private Long checkNumber;
    @ApiModelProperty("巡检时间")
    @ExcelProperty(value = "巡检时间")
    private Date createTime;
    @ApiModelProperty("创建人")
    @ExcelProperty(value = "巡检人")
    private String createBy;
    @ApiModelProperty("巡检类型")
    @ExcelProperty(value = "巡检类型")
    private String checkType;
}
