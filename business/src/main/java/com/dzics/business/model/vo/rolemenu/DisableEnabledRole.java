package com.dzics.business.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Data
public class DisableEnabledRole {
    @ApiModelProperty("角色id")
    @NotNull(message = "请选择角色")
    private String roleId;
    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @NotNull(message = "状态必传")
    private Integer status;
}
