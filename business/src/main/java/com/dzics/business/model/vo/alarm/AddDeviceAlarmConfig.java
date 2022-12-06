package com.dzics.business.model.vo.alarm;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2021/6/21.
 * @since
 */
@Data
public class AddDeviceAlarmConfig implements Serializable {
    @ApiModelProperty(value = "数据唯一ID", required = false)
    @ExcelIgnore
    private String alarmConfigId;

    @ExcelProperty("订单ID")
    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID必填")
    private String orderId;

    @ExcelProperty("产线ID")
    @ApiModelProperty(value = "产线ID", required = true)
    @NotNull(message = "产线ID必传")
    private String lineId;

    @ExcelProperty("设备ID")
    @ApiModelProperty(value = "设备ID", required = true)
    @NotNull(message = "设备ID必传")
    private String deviceId;

    @ExcelProperty("告警内容")
    @ApiModelProperty(value = "告警内容", required = true)
    @NotNull(message = "告警内容必填")
    private String alarmName;

    @ExcelProperty("数据解析位置")
    @ApiModelProperty(value = "数据解析位置", required = true)
    @NotNull(message = "数据解析位置必填")
    private Integer locationData;

    @ExcelProperty("告警等级")
    @ApiModelProperty("告警等级")
    private Integer alarmGrade;
}
