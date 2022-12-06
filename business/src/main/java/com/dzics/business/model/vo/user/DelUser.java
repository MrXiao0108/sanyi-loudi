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
public class DelUser {
    @ApiModelProperty("用户id")
    @NotNull(message = "请选择用户")
    private Long id;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "登录账号必填")
    private String username;

}
