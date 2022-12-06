package com.dzics.common.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class GetGroupConfig {
    @ApiModelProperty(value = "组id", required = true)
    @NotNull(message = "组id必传")
    private String groupId;
}
