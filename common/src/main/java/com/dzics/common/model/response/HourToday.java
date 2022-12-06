package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年06月02日 15:48
 */
@Data
public class HourToday {
    @ApiModelProperty("小时")
    private Integer hour;
    @ApiModelProperty("产量")
    private Long sumToday;
    @ApiModelProperty("设备id")
    private Long deviceId;
}
