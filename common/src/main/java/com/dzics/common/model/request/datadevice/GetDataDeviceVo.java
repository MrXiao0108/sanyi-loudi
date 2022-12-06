package com.dzics.common.model.request.datadevice;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetDataDeviceVo extends PageLimit {

    @ApiModelProperty(value = "第三方设备id")
    private Long deviceId;
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ApiModelProperty(value = "设备类型（1.搬运机器人，2.焊接机器人，3.数控设备，4.清洗机，5.检测设备）")
    private Integer deviceType;
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    private String lineId;
}
