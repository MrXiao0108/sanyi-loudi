package com.dzics.business.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Data
public class DelRole {
    @ApiModelProperty("角色id")
    @NotNull(message = "请选择角色")
    private Long id;
}
