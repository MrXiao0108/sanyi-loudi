package com.dzics.kanban.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 看板传递参数
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class KbParmsMethod {
    /**
     * 方法组名称
     */
    @ApiModelProperty("方法组名称")
    @NotNull(message = "方法组名称必填")
    private String methodGroup;
}
