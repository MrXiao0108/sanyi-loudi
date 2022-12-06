package com.dzics.business.model.vo.alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/6/21.
 * @since
 */
@Data
public class AddGiveAlarmConfig {
    @ApiModelProperty(value = "数据唯一ID",required = false)
    private String alarmConfigId;
    @ApiModelProperty(value = "订单ID",required = true)
    @NotNull(message = "订单ID必填")
    private String orderId;
    @ApiModelProperty(value = "设备编号",required = true)
    @NotNull(message = "设备编号必填")
    private String bandNumber;
    @NotNull(message = "告警内容必填")
    @ApiModelProperty(value = "告警内容",required = true)
    private String alarmName;
    @NotNull(message = "告警地址必填")
    @ApiModelProperty(value = "告警地址",required = true)
    private String alarmAddress;
    @NotNull(message = "IO地址必填")
    @ApiModelProperty(value = "IO地址",required = true)
    private String alarmIoAddress;
    @NotNull(message = "传输带名称必填")
    @ApiModelProperty(value = "传输带名称",required = true)
    private String bandName;
    @NotNull(message = "数据解析位置必填")
    @ApiModelProperty(value = "数据解析位置",required = true)
    private Integer locationData;
    @NotNull(message = "告警类型必填")
    @ApiModelProperty(value = "告警类型",required = true)
    private String alarmType;
}
