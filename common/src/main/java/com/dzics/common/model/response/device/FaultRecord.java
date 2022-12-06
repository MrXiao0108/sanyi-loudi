package com.dzics.common.model.response.device;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.write.FaultTypeConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since 设备故障记录
 */
@Data
public class FaultRecord {
    @ApiModelProperty("维修记录ID")
    @ExcelIgnore
    private String repairId;

    @ApiModelProperty("单号")
    @ExcelProperty(value = "故障台账单号",index = 3)
    private String checkNumber;

    @ApiModelProperty("备注")
    @ExcelProperty(value = "故障备注",index = 4)
    private String remarks;

    @ApiModelProperty("开始处理时间")
    @ExcelProperty(value = "维修开始时间",index = 6)
    private String startHandleDate;

    @ApiModelProperty("处理完成时间")
    @ExcelProperty(value = "维修结束时间",index = 7)
    private String completeHandleDate;

    @ApiModelProperty("故障类型 1 紧急 2 突发 3 一般")
    @ExcelProperty(value = "故障类型",index = 5,converter = FaultTypeConverter.class)
    private String faultType;

    @ApiModelProperty("故障处理人")
    @ExcelProperty(value = "故障处理人",index = 8)
    private String createBy;

    @ApiModelProperty("产线ID")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty("产线名称")
    @ExcelProperty(value = "产线名称",index = 0)
    private String lineName;

    @ApiModelProperty("设备名称")
    @ExcelProperty(value = "设备名称",index = 1)
    private String equipmentName;

    @ApiModelProperty("设备编号")
    @ExcelProperty(value = "设备编号",index = 2)
    private String equipmentNo;

    @ApiModelProperty("设备id")
    @ExcelIgnore
    private String deviceId;
}
