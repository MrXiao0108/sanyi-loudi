package com.dzics.kanban.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除接口参数
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class DelInterfaceMethod {
    @ApiModelProperty(value = "接口id", required = true)
    @NotNull(message = "接口id必填")
    private String interfaceId;
}
