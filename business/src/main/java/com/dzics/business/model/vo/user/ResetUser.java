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
public class ResetUser {
    @ApiModelProperty("用户id")
    @NotNull(message = "请选择用户")
    private String userId;
    @ApiModelProperty("密码")
    @NotBlank(message = "用户密码必填")
    private String password;
    @ApiModelProperty(value = "登录账号")
    @NotBlank(message = "登录账号必填")
    private String username;
}
