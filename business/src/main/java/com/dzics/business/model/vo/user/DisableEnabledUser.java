package com.dzics.business.model.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Data
public class DisableEnabledUser {
    @ApiModelProperty("用户id")
    @NotNull(message = "请选择用户")
    private String id;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @NotNull(message = "状态必选")
    private Integer status;

    @ApiModelProperty(value = "登录账号")
    @NotBlank(message = "登录账号必填")
    private String username;
}
