package com.dzics.kanban.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除组
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class DelInterfaceGroup {
    @ApiModelProperty(value = "组id",required = true)
    @NotNull(message = "组id必传")
    private String groupId;
}
