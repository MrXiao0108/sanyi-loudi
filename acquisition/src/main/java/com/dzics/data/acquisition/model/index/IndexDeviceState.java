package com.dzics.data.acquisition.model.index;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页设备状态
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Data
public class IndexDeviceState {
    @ApiModelProperty("连接状态")
    private String connectState;
    @ApiModelProperty("运行状态")
    private String runStatus;
    @ApiModelProperty("告警信息")
    private String alarmStatus;

}
