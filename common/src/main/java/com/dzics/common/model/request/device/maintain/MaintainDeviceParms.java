package com.dzics.common.model.request.device.maintain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 保养记录
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Data
public class MaintainDeviceParms {
    /**
     * 产线ID
     */
    @ApiModelProperty("产线ID")
    private String lineId;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("状态 1已保养,2超时未保养")
    private String states;


}
