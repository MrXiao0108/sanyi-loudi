package com.dzics.common.model.response.device.maintain;

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
public class MaintainRecord {
    @ApiModelProperty(value = "保养记录ID")
    private String maintainHistoryId;
    @ApiModelProperty(value = "保养时间")
    private String maintainDate;
    @ApiModelProperty(value = "产线名称")
    private String lineName;
    @ApiModelProperty(value = "设备名称")
    private String equipmentName;
    @ApiModelProperty(value = "设备编号")
    private String equipmentNo;
    @ApiModelProperty(value = "保养人")
    private String createBy;
}
