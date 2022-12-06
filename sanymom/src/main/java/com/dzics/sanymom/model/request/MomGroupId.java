package com.dzics.sanymom.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2022/1/21.
 * @since
 */
@Data
public class MomGroupId {
    @ApiModelProperty(value = "日志ID",required = true)
    @NotNull(message = "日志ID必填")
    private String logId;
}
