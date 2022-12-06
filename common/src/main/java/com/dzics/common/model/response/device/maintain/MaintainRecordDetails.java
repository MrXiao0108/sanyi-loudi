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
public class MaintainRecordDetails {
    @ApiModelProperty(value = "保养项")
    private String maintainItem;
    @ApiModelProperty(value = "保养内容")
    private String maintainContent;
}
