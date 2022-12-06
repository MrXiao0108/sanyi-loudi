package com.dzics.common.model.response.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Data
public class DeviceMessage {
    @ApiModelProperty(value = "设备ID")
    private String deviceId;
    /**
     * 设备序号
     */
    @ApiModelProperty(value = "设备序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    private Integer equipmentType;

    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    @ApiModelProperty(value = "设备名称")
    private String equipmentName;

    @ApiModelProperty(value = "昵称")
    private String nickName;
}
