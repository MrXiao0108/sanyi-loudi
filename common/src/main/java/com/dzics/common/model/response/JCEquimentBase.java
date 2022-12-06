package com.dzics.common.model.response;

import com.dzics.common.enums.DeviceSocketSendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备基础字段信息
 *
 * @author ZhangChengJun
 * Date 2021/3/2.
 * @since
 */
@Data
public class JCEquimentBase<T> {
    @ApiModelProperty("RUNSTATUS 状态信息；DOWNSTATUS 停机次数，PROSUM 生产数量,UTILIZATION 稼动")
    private String type = DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DEVICE.getInfo();

    @ApiModelProperty("设备信息")
    T data;

}
