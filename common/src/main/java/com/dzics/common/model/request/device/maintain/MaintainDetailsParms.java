package com.dzics.common.model.request.device.maintain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 保养记录
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Data
public class MaintainDetailsParms {
    @ApiModelProperty(value = "保养记录ID",required = true)
    @NotNull(message = "保养记录ID必填")
    private String maintainHistoryId;


}
